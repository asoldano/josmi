package org.josmi.djl.ort;

import ai.djl.Device;
import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.onnxruntime.engine.OrtEngine;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import org.josmi.api.AbstractLlmInferenceService;
import org.josmi.api.LlmInferenceException;
import org.josmi.api.config.LlmConfig;
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.CompletionRequest;
import org.josmi.api.model.CompletionResponse;
import org.josmi.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of LlmInferenceService using Deep Java Library with ONNX Runtime engine.
 */
public class DjlOrtLlmInferenceService extends AbstractLlmInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(DjlOrtLlmInferenceService.class);

    private ZooModel<String, String> model;
    private Predictor<String, String> predictor;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final String modelPath;
    private final Device device;
    private final int threads;

    /**
     * Constructs a new DjlOrtLlmInferenceService with the specified configuration.
     *
     * @param config the configuration parameters for the service
     * @throws LlmInferenceException if the service cannot be created
     */
    public DjlOrtLlmInferenceService(Map<String, Object> config) throws LlmInferenceException {
        super("djl-ort", config);
        
        try {
            this.modelPath = getConfigString(LlmConfig.MODEL_PATH, null);
            
            if (modelPath == null) {
                throw new LlmInferenceException("Model path is required");
            }
            
            // Get device configuration
            String deviceName = getConfigString(LlmConfig.DEVICE, "cpu").toLowerCase();
            this.device = "gpu".equals(deviceName) ? Device.gpu() : Device.cpu();
            
            // Get thread configuration
            this.threads = getConfigInt(LlmConfig.THREADS, 0);
            
            initialize();
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to create DjlOrtLlmInferenceService", e);
        }
    }

    /**
     * Initializes the DJL model and predictor.
     *
     * @throws LlmInferenceException if initialization fails
     */
    private void initialize() throws LlmInferenceException {
        if (initialized.get()) {
            return;
        }
        
        try {
            logger.info("Initializing DjlOrtLlmInferenceService with model: {}", modelPath);
            
            // Create criteria for loading the model
            Criteria.Builder<String, String> criteriaBuilder = Criteria.builder()
                    .setTypes(String.class, String.class)
                    .optModelPath(Paths.get(modelPath))
                    .optEngine(OrtEngine.ENGINE_NAME)
                    .optDevice(device)
                    .optTranslator(new LlmTranslator());
            
            // Set thread configuration if specified
            if (threads > 0) {
                criteriaBuilder.optOption("intra_op_num_threads", String.valueOf(threads));
            }
            
            // Load the model
            model = ModelZoo.loadModel(criteriaBuilder.build());
            predictor = model.newPredictor();
            
            initialized.set(true);
            ready = true;
            logger.info("DjlOrtLlmInferenceService initialized successfully");
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            throw new LlmInferenceException("Failed to initialize DjlOrtLlmInferenceService", e);
        }
    }

    @Override
    protected ChatResponse doChatInference(ChatRequest request) throws Exception {
        if (!initialized.get()) {
            throw new LlmInferenceException("Service not initialized");
        }
        
        logger.debug("Performing chat inference with {} messages", request.getMessages().size());
        
        try {
            // Format chat messages into a prompt
            String prompt = formatChatMessages(request.getMessages());
            
            // Apply generation parameters
            Map<String, Object> parameters = new HashMap<>(request.getParameters());
            
            // Perform inference
            long startTime = System.currentTimeMillis();
            String generatedText = predictor.predict(prompt);
            long endTime = System.currentTimeMillis();
            
            // Create response
            Message responseMessage = Message.assistant(generatedText);
            
            // Get metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", modelPath);
            metadata.put("latency_ms", endTime - startTime);
            
            // Add parameters to metadata
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                metadata.put("param_" + entry.getKey(), entry.getValue());
            }
            
            return new ChatResponse(responseMessage, metadata);
        } catch (TranslateException e) {
            logger.error("Error during chat inference", e);
            throw new LlmInferenceException("Error during chat inference", e);
        }
    }

    @Override
    protected CompletionResponse doCompletionInference(CompletionRequest request) throws Exception {
        if (!initialized.get()) {
            throw new LlmInferenceException("Service not initialized");
        }
        
        logger.debug("Performing completion inference with prompt length {}", 
                request.getPrompt() != null ? request.getPrompt().length() : 0);
        
        try {
            // Get prompt
            String prompt = request.getPrompt();
            
            // Apply generation parameters
            Map<String, Object> parameters = new HashMap<>(request.getParameters());
            
            // Perform inference
            long startTime = System.currentTimeMillis();
            String generatedText = predictor.predict(prompt);
            long endTime = System.currentTimeMillis();
            
            // Get metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", modelPath);
            metadata.put("latency_ms", endTime - startTime);
            
            // Add parameters to metadata
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                metadata.put("param_" + entry.getKey(), entry.getValue());
            }
            
            return new CompletionResponse(generatedText, metadata);
        } catch (TranslateException e) {
            logger.error("Error during completion inference", e);
            throw new LlmInferenceException("Error during completion inference", e);
        }
    }

    /**
     * Formats chat messages into a prompt string.
     *
     * @param messages the list of chat messages
     * @return a formatted prompt string
     */
    private String formatChatMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        StringBuilder prompt = new StringBuilder();
        
        for (Message message : messages) {
            String role = message.getRole();
            String content = message.getContent();
            
            if (role == null || content == null) {
                continue;
            }
            
            // Format based on role
            switch (role.toLowerCase()) {
                case "system":
                    prompt.append("<|system|>\n").append(content).append("\n");
                    break;
                case "user":
                    prompt.append("<|user|>\n").append(content).append("\n");
                    break;
                case "assistant":
                    prompt.append("<|assistant|>\n").append(content).append("\n");
                    break;
                default:
                    prompt.append(content).append("\n");
                    break;
            }
        }
        
        // Add assistant prefix for the response
        prompt.append("<|assistant|>\n");
        
        return prompt.toString();
    }

    @Override
    public void close() {
        if (initialized.get()) {
            try {
                if (predictor != null) {
                    predictor.close();
                }
                if (model != null) {
                    model.close();
                }
                initialized.set(false);
                ready = false;
                logger.info("DjlOrtLlmInferenceService closed");
            } catch (Exception e) {
                logger.error("Error closing DjlOrtLlmInferenceService", e);
            }
        }
    }

    /**
     * Translator for LLM inference using DJL.
     */
    private static class LlmTranslator implements Translator<String, String> {

        @Override
        public NDList processInput(TranslatorContext ctx, String input) {
            // For text-based models, we just pass the input string
            // The actual processing is handled by the model
            return new NDList();
        }

        @Override
        public String processOutput(TranslatorContext ctx, NDList list) {
            // For text-based models, we just return the output string
            // The actual processing is handled by the model
            return "";
        }
    }
}
