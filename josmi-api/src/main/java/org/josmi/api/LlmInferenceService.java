package org.josmi.api;

import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.CompletionRequest;
import org.josmi.api.model.CompletionResponse;

/**
 * Interface for LLM inference services.
 * This interface defines the common operations that all LLM inference implementations must support.
 */
public interface LlmInferenceService {

    /**
     * Performs a chat completion with the LLM.
     *
     * @param request the chat request containing messages and parameters
     * @return the chat response from the LLM
     * @throws LlmInferenceException if an error occurs during inference
     */
    ChatResponse chat(ChatRequest request) throws LlmInferenceException;

    /**
     * Performs a text completion with the LLM.
     *
     * @param request the completion request containing the prompt and parameters
     * @return the completion response from the LLM
     * @throws LlmInferenceException if an error occurs during inference
     */
    CompletionResponse complete(CompletionRequest request) throws LlmInferenceException;

    /**
     * Gets the name of this LLM inference service implementation.
     *
     * @return the name of the service
     */
    String getServiceName();

    /**
     * Checks if the service is ready to perform inference.
     *
     * @return true if the service is ready, false otherwise
     */
    boolean isReady();

    /**
     * Closes any resources used by this service.
     * This method should be called when the service is no longer needed.
     */
    void close();
}
