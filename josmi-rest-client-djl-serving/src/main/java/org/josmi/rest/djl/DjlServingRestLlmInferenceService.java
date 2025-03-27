package org.josmi.rest.djl;

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
import org.josmi.rest.djl.client.DjlServingRestClient;
import org.josmi.rest.djl.dto.DjlServingRequestDto;
import org.josmi.rest.djl.dto.DjlServingResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of LlmInferenceService using a REST client for a DJL Serving backend.
 */
public class DjlServingRestLlmInferenceService extends AbstractLlmInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(DjlServingRestLlmInferenceService.class);

    private final String endpointUrl;
    private final int timeoutMs;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private ResteasyClient client;
    private DjlServingRestClient restClient;

    /**
     * Constructs a new DjlServingRestLlmInferenceService with the specified configuration.
     *
     * @param config the configuration parameters for the service
     * @throws LlmInferenceException if the service cannot be created
     */
    public DjlServingRestLlmInferenceService(Map<String, Object> config) throws LlmInferenceException {
        super("djl-serving-rest", config);
        
        try {
            this.endpointUrl = getConfigString(LlmConfig.ENDPOINT_URL, null);
            
            if (endpointUrl == null) {
                throw new LlmInferenceException("Endpoint URL is required");
            }
            
            this.timeoutMs = getConfigInt(LlmConfig.TIMEOUT_MS, 30000);
            
            initialize();
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to create DjlServingRestLlmInferenceService", e);
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
            logger.info("Initializing DjlServingRestLlmInferenceService with endpoint: {}", endpointUrl);
            
            // Create REST client
            client = ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                    .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .build();
            
            URI uri = UriBuilder.fromUri(endpointUrl).build();
            ResteasyWebTarget target = client.target(uri);
            
            restClient = target.proxy(DjlServingRestClient.class);
            
            // Check if the backend is ready
            try {
                Map<String, Object> pingResponse = restClient.ping();
                boolean isReady = pingResponse != null && "Healthy".equals(pingResponse.get("status"));
                if (!isReady) {
                    logger.warn("Backend is not ready");
                }
                ready = isReady;
            } catch (Exception e) {
                logger.warn("Failed to check if backend is ready", e);
                ready = false;
            }
            
            initialized.set(true);
            logger.info("DjlServingRestLlmInferenceService initialized successfully");
        } catch (Exception e) {
            throw new LlmInferenceException("Failed to initialize DjlServingRestLlmInferenceService", e);
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
            DjlServingRequestDto requestDto = DjlServingRequestDto.fromChatRequest(request);
            
            // Send request to backend
            long startTime = System.currentTimeMillis();
            DjlServingResponseDto responseDto = restClient.predict(requestDto);
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
            DjlServingRequestDto requestDto = DjlServingRequestDto.fromCompletionRequest(request);
            
            // Send request to backend
            long startTime = System.currentTimeMillis();
            DjlServingResponseDto responseDto = restClient.predict(requestDto);
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
            Map<String, Object> pingResponse = restClient.ping();
            return pingResponse != null && "Healthy".equals(pingResponse.get("status"));
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
                logger.info("DjlServingRestLlmInferenceService closed");
            } catch (Exception e) {
                logger.error("Error closing DjlServingRestLlmInferenceService", e);
            }
        }
    }
}
