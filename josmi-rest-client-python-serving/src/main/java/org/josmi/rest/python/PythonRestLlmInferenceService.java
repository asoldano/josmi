package org.josmi.rest.python;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.josmi.api.AbstractLlmInferenceService;
import org.josmi.api.LlmInferenceException;
import org.josmi.api.config.LlmConfig;
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.CompletionRequest;
import org.josmi.api.model.CompletionResponse;
import org.josmi.rest.python.client.PythonLlmRestClient;
import org.josmi.rest.python.dto.ChatRequestDto;
import org.josmi.rest.python.dto.ChatResponseDto;
import org.josmi.rest.python.dto.CompletionRequestDto;
import org.josmi.rest.python.dto.CompletionResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of LlmInferenceService using a REST client for a Python FastAPI backend.
 */
public class PythonRestLlmInferenceService extends AbstractLlmInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(PythonRestLlmInferenceService.class);

    private final String endpointUrl;
    private final int timeoutMs;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private ResteasyClient client;
    private PythonLlmRestClient restClient;

    /**
     * Constructs a new PythonRestLlmInferenceService with the specified configuration.
     *
     * @param config the configuration parameters for the service
     * @throws LlmInferenceException if the service cannot be created
     */
    public PythonRestLlmInferenceService(Map<String, Object> config) throws LlmInferenceException {
        super("python-rest", config);
        
        try {
            this.endpointUrl = getConfigString(LlmConfig.ENDPOINT_URL, null);
            
            if (endpointUrl == null) {
                throw new LlmInferenceException("Endpoint URL is required");
            }
            
            this.timeoutMs = getConfigInt(LlmConfig.TIMEOUT_MS, 30000);
            
            initialize();
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to create PythonRestLlmInferenceService", e);
        }
    }

    /**
     * Initializes the REST client.
     *
     * @throws LlmInferenceException if initialization fails
     */
    private void initialize() throws LlmInferenceException {
        if (initialized.get()) {
            return;
        }
        
        try {
            logger.info("Initializing PythonRestLlmInferenceService with endpoint: {}", endpointUrl);
            
            // Create REST client
            client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                    .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .build();
            
            URI uri = UriBuilder.fromUri(endpointUrl).build();
            ResteasyWebTarget target = client.target(uri);
            
            restClient = target.proxy(PythonLlmRestClient.class);
            
            // Check if the backend is ready
            try {
                boolean isReady = restClient.isReady();
                if (!isReady) {
                    logger.warn("Backend is not ready");
                }
                ready = isReady;
            } catch (Exception e) {
                logger.warn("Failed to check if backend is ready", e);
                ready = false;
            }
            
            initialized.set(true);
            logger.info("PythonRestLlmInferenceService initialized successfully");
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to initialize PythonRestLlmInferenceService", e);
        }
    }

    @Override
    protected ChatResponse doChatInference(ChatRequest request) throws Exception {
        if (!initialized.get()) {
            throw new LlmInferenceException("Service not initialized");
        }
        
        logger.debug("Performing chat inference with {} messages", request.getMessages().size());
        
        try {
            // Convert request to DTO
            ChatRequestDto requestDto = ChatRequestDto.fromChatRequest(request);
            
            // Send request to backend
            long startTime = System.currentTimeMillis();
            ChatResponseDto responseDto = restClient.chat(requestDto);
            long endTime = System.currentTimeMillis();
            
            // Convert response from DTO
            ChatResponse response = responseDto.toChatResponse();
            
            // Add latency to metadata if not already present
            if (!response.getMetadata().containsKey("latency_ms")) {
                response.getMetadata().put("latency_ms", endTime - startTime);
            }
            
            return response;
        } catch (ProcessingException e) {
            logger.error("Error during chat inference", e);
            throw new LlmInferenceException("Error during chat inference: " + e.getMessage(), e);
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
            // Convert request to DTO
            CompletionRequestDto requestDto = CompletionRequestDto.fromCompletionRequest(request);
            
            // Send request to backend
            long startTime = System.currentTimeMillis();
            CompletionResponseDto responseDto = restClient.complete(requestDto);
            long endTime = System.currentTimeMillis();
            
            // Convert response from DTO
            CompletionResponse response = responseDto.toCompletionResponse();
            
            // Add latency to metadata if not already present
            if (!response.getMetadata().containsKey("latency_ms")) {
                response.getMetadata().put("latency_ms", endTime - startTime);
            }
            
            return response;
        } catch (ProcessingException e) {
            logger.error("Error during completion inference", e);
            throw new LlmInferenceException("Error during completion inference: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isReady() {
        if (!initialized.get()) {
            return false;
        }
        
        try {
            return restClient.isReady();
        } catch (Exception e) {
            logger.warn("Failed to check if backend is ready", e);
            return false;
        }
    }

    @Override
    public void close() {
        if (initialized.get()) {
            try {
                if (client != null) {
                    client.close();
                }
                initialized.set(false);
                ready = false;
                logger.info("PythonRestLlmInferenceService closed");
            } catch (Exception e) {
                logger.error("Error closing PythonRestLlmInferenceService", e);
            }
        }
    }
}
