package org.josmi.api;

/**
 * Exception thrown when an error occurs during LLM inference.
 */
public class LlmInferenceException extends Exception {

    /**
     * Constructs a new LlmInferenceException with the specified detail message.
     *
     * @param message the detail message
     */
    public LlmInferenceException(String message) {
        super(message);
    }

    /**
     * Constructs a new LlmInferenceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public LlmInferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new LlmInferenceException with the specified cause.
     *
     * @param cause the cause
     */
    public LlmInferenceException(Throwable cause) {
        super(cause);
    }
}
