package org.josmi.ort;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.genai.SimpleGenAI;
import ai.onnxruntime.genai.GeneratorParams;
import ai.onnxruntime.genai.Model;
import ai.onnxruntime.genai.Tokenizer;
import ai.onnxruntime.genai.GenAIException;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of LlmInferenceService using ONNX Runtime Java API.
 */
public class OrtLlmInferenceService extends AbstractLlmInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(OrtLlmInferenceService.class);

    private final OrtEnvironment environment;
    private OrtSession session;
    private SimpleGenAI simpleGenAI;
    private Model model;
    private Tokenizer tokenizer;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final String modelPath;
    private final String tokenizerPath;

    /**
     * Constructs a new OrtLlmInferenceService with the specified configuration.
     *
     * @param config the configuration parameters for the service
     * @throws LlmInferenceException if the service cannot be created
     */
    public OrtLlmInferenceService(Map<String, Object> config) throws LlmInferenceException {
        super("ort", config);
        
        try {
            this.environment = OrtEnvironment.getEnvironment();
            this.modelPath = getConfigString(LlmConfig.MODEL_PATH, null);
            this.tokenizerPath = getConfigString(LlmConfig.TOKENIZER_PATH, null);
            
            if (modelPath == null) {
                throw new LlmInferenceException("Model path is required");
            }
            
            initialize();
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to create OrtLlmInferenceService", e);
        }
    }

    /**
     * Initializes the ONNX Runtime session and SimpleGenAI instance.
     *
     * @throws LlmInferenceException if initialization fails
     */
    private void initialize() throws LlmInferenceException {
        if (initialized.get()) {
            return;
        }
        
        try {
            logger.info("Initializing OrtLlmInferenceService with model: {}", modelPath);
            
            // Create session options
            OrtSession.SessionOptions sessionOptions = new OrtSession.SessionOptions();
            
            // Set number of threads if specified
            int threads = getConfigInt(LlmConfig.THREADS, 0);
            if (threads > 0) {
                sessionOptions.setIntraOpNumThreads(threads);
            }
            
            // Load the model
            Path modelFilePath = Paths.get(modelPath);
            session = environment.createSession(modelFilePath.toString(), sessionOptions);
            
            // Initialize SimpleGenAI
            if (tokenizerPath != null) {
                Path tokenizerFilePath = Paths.get(tokenizerPath);
                simpleGenAI = new SimpleGenAI(modelPath);
            } else {
                simpleGenAI = new SimpleGenAI(modelPath);
            }
            
            initialized.set(true);
            ready = true;
            logger.info("OrtLlmInferenceService initialized successfully");
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to initialize OrtLlmInferenceService", e);
        }
    }

    @Override
    protected ChatResponse doChatInference(ChatRequest request) throws Exception {
        if (!initialized.get()) {
            throw new LlmInferenceException("Service not initialized");
        }
        
        logger.debug("Performing chat inference with {} messages", request.getMessages().size());
        
        try {
            // Format all messages into a single prompt
            String prompt = formatChatMessages(request.getMessages());
            
            // Create generator parameters
            GeneratorParams params = simpleGenAI.createGeneratorParams(prompt);
            
            // Apply generation parameters
            applyGenerationParameters(params, request.getParameters());
            
            // Generate response
            StringBuilder generatedText = new StringBuilder();
            simpleGenAI.generate(params, token -> generatedText.append(token));
            String result = generatedText.toString();
            
            // Create response
            Message responseMessage = Message.assistant(result);
            
            // Get metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", modelPath);
            
            return new ChatResponse(responseMessage, metadata);
        } catch (Exception e) {
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
            // Create generator parameters
            GeneratorParams params = simpleGenAI.createGeneratorParams(request.getPrompt());
            
            // Apply generation parameters
            applyGenerationParameters(params, request.getParameters());
            
            // Generate response
            StringBuilder generatedText = new StringBuilder();
            simpleGenAI.generate(params, token -> generatedText.append(token));
            String result = generatedText.toString();
            
            // Get metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", modelPath);
            
            return new CompletionResponse(result, metadata);
        } catch (Exception e) {
            logger.error("Error during completion inference", e);
            throw new LlmInferenceException("Error during completion inference", e);
        }
    }

    /**
     * Formats a list of chat messages into a single prompt string.
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
            prompt.append(formatMessage(message));
        }
        
        return prompt.toString();
    }

    /**
     * Formats a message for the model.
     *
     * @param message the message to format
     * @return the formatted message
     */
    private String formatMessage(Message message) {
        if (message == null) {
            return "";
        }
        
        String role = message.getRole();
        String content = message.getContent();
        
        if (role == null || content == null) {
            return content != null ? content : "";
        }
        
        // Format based on role
        switch (role.toLowerCase()) {
            case "system":
                return "<|system|>\n" + content + "\n";
            case "user":
                return "<|user|>\n" + content + "\n";
            case "assistant":
                return "<|assistant|>\n" + content + "\n";
            default:
                return content;
        }
    }

    /**
     * Applies generation parameters to a GeneratorParams object.
     *
     * @param params the GeneratorParams to apply parameters to
     * @param parameters the parameters to apply
     * @throws GenAIException if there is an error setting search options
     */
    private void applyGenerationParameters(GeneratorParams params, Map<String, Object> parameters) throws GenAIException {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }
        
        try {
            // Apply temperature
            if (parameters.containsKey(LlmConfig.TEMPERATURE)) {
                double temperature = getDoubleParameter(parameters, LlmConfig.TEMPERATURE, 1.0);
                params.setSearchOption("temperature", temperature);
            }
            
            // Apply top-p
            if (parameters.containsKey(LlmConfig.TOP_P)) {
                double topP = getDoubleParameter(parameters, LlmConfig.TOP_P, 1.0);
                params.setSearchOption("top_p", topP);
            }
            
            // Apply max tokens
            if (parameters.containsKey(LlmConfig.MAX_TOKENS) || parameters.containsKey("max_tokens")) {
                int maxTokens = getIntParameter(parameters, LlmConfig.MAX_TOKENS, 
                        getIntParameter(parameters, "max_tokens", 1024));
                params.setSearchOption("max_length", (double)maxTokens);
            }
            
            // Apply repetition penalty
            if (parameters.containsKey(LlmConfig.REPETITION_PENALTY)) {
                double repetitionPenalty = getDoubleParameter(parameters, LlmConfig.REPETITION_PENALTY, 1.0);
                params.setSearchOption("repetition_penalty", repetitionPenalty);
            }
        } catch (GenAIException e) {
            logger.warn("Error setting search options: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Gets a double parameter from a map.
     *
     * @param parameters the parameter map
     * @param key the parameter key
     * @param defaultValue the default value to return if the parameter is not found or not a double
     * @return the parameter value as a double, or the default value if not found or not a double
     */
    private double getDoubleParameter(Map<String, Object> parameters, String key, double defaultValue) {
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Gets an integer parameter from a map.
     *
     * @param parameters the parameter map
     * @param key the parameter key
     * @param defaultValue the default value to return if the parameter is not found or not an integer
     * @return the parameter value as an integer, or the default value if not found or not an integer
     */
    private int getIntParameter(Map<String, Object> parameters, String key, int defaultValue) {
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Override
    public void close() {
        if (initialized.get()) {
            try {
                // SimpleGenAI doesn't have a close method
                if (session != null) {
                    session.close();
                }
                initialized.set(false);
                ready = false;
                logger.info("OrtLlmInferenceService closed");
            } catch (Exception e) {
                logger.error("Error closing OrtLlmInferenceService", e);
            }
        }
    }
}
