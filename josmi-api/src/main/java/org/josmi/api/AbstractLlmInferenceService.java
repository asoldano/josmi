package org.josmi.api;

import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.CompletionRequest;
import org.josmi.api.model.CompletionResponse;
import org.josmi.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for LlmInferenceService implementations.
 * Provides common functionality that can be reused across different implementations.
 */
public abstract class AbstractLlmInferenceService implements LlmInferenceService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLlmInferenceService.class);
    
    protected final String serviceName;
    protected final Map<String, Object> config;
    protected boolean ready = false;

    /**
     * Constructs a new AbstractLlmInferenceService with the specified name and configuration.
     *
     * @param serviceName the name of the service
     * @param config the configuration parameters for the service
     */
    protected AbstractLlmInferenceService(String serviceName, Map<String, Object> config) {
        this.serviceName = serviceName;
        this.config = new HashMap<>(config);
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public ChatResponse chat(ChatRequest request) throws LlmInferenceException {
        if (!isReady()) {
            throw new LlmInferenceException("Service is not ready");
        }
        
        logger.debug("Processing chat request with {} messages", request.getMessages().size());
        long startTime = System.currentTimeMillis();
        
        try {
            ChatResponse response = doChatInference(request);
            
            // Add latency to metadata if not already present
            if (!response.getMetadata().containsKey("latency_ms")) {
                response.getMetadata().put("latency_ms", System.currentTimeMillis() - startTime);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error during chat inference", e);
            throw new LlmInferenceException("Error during chat inference", e);
        }
    }

    @Override
    public CompletionResponse complete(CompletionRequest request) throws LlmInferenceException {
        if (!isReady()) {
            throw new LlmInferenceException("Service is not ready");
        }
        
        logger.debug("Processing completion request with prompt length {}", 
                request.getPrompt() != null ? request.getPrompt().length() : 0);
        long startTime = System.currentTimeMillis();
        
        try {
            CompletionResponse response = doCompletionInference(request);
            
            // Add latency to metadata if not already present
            if (!response.getMetadata().containsKey("latency_ms")) {
                response.getMetadata().put("latency_ms", System.currentTimeMillis() - startTime);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error during completion inference", e);
            throw new LlmInferenceException("Error during completion inference", e);
        }
    }

    /**
     * Performs the actual chat inference.
     * This method must be implemented by subclasses.
     *
     * @param request the chat request
     * @return the chat response
     * @throws Exception if an error occurs during inference
     */
    protected abstract ChatResponse doChatInference(ChatRequest request) throws Exception;

    /**
     * Performs the actual completion inference.
     * This method must be implemented by subclasses.
     *
     * @param request the completion request
     * @return the completion response
     * @throws Exception if an error occurs during inference
     */
    protected abstract CompletionResponse doCompletionInference(CompletionRequest request) throws Exception;

    /**
     * Utility method to convert a completion request to a chat request.
     * This can be useful for implementations that only support one of the two interfaces.
     *
     * @param request the completion request to convert
     * @return a chat request equivalent to the completion request
     */
    protected ChatRequest completionToChatRequest(CompletionRequest request) {
        List<Message> messages = new ArrayList<>();
        messages.add(Message.user(request.getPrompt()));
        
        return ChatRequest.builder()
                .addMessages(messages)
                .setParameters(request.getParameters())
                .build();
    }

    /**
     * Utility method to convert a chat response to a completion response.
     * This can be useful for implementations that only support one of the two interfaces.
     *
     * @param response the chat response to convert
     * @return a completion response equivalent to the chat response
     */
    protected CompletionResponse chatToCompletionResponse(ChatResponse response) {
        return CompletionResponse.builder()
                .text(response.getContent())
                .setMetadata(response.getMetadata())
                .build();
    }

    /**
     * Gets a configuration value as a string.
     *
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found
     * @return the configuration value as a string, or the default value if not found
     */
    protected String getConfigString(String key, String defaultValue) {
        Object value = config.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Gets a configuration value as an integer.
     *
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found or not an integer
     * @return the configuration value as an integer, or the default value if not found or not an integer
     */
    protected int getConfigInt(String key, int defaultValue) {
        Object value = config.get(key);
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

    /**
     * Gets a configuration value as a double.
     *
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found or not a double
     * @return the configuration value as a double, or the default value if not found or not a double
     */
    protected double getConfigDouble(String key, double defaultValue) {
        Object value = config.get(key);
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
     * Gets a configuration value as a boolean.
     *
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found or not a boolean
     * @return the configuration value as a boolean, or the default value if not found or not a boolean
     */
    protected boolean getConfigBoolean(String key, boolean defaultValue) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }
}
