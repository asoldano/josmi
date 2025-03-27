package org.josmi.api;

import java.util.Map;

/**
 * Factory interface for creating LlmInferenceService instances.
 * Each implementation module will provide its own factory implementation.
 */
public interface LlmInferenceServiceFactory {

    /**
     * Creates a new LlmInferenceService instance with the specified configuration.
     *
     * @param config the configuration parameters for the service
     * @return a new LlmInferenceService instance
     * @throws LlmInferenceException if the service cannot be created
     */
    LlmInferenceService create(Map<String, Object> config) throws LlmInferenceException;

    /**
     * Gets the name of the factory implementation.
     *
     * @return the name of the factory
     */
    String getFactoryName();

    /**
     * Gets the description of the factory implementation.
     *
     * @return the description of the factory
     */
    String getDescription();
}
