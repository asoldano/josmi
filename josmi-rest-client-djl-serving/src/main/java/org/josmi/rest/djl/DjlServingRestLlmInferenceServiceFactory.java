package org.josmi.rest.djl;

import org.josmi.api.LlmInferenceException;
import org.josmi.api.LlmInferenceService;
import org.josmi.api.LlmInferenceServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Factory for creating DjlServingRestLlmInferenceService instances.
 */
public class DjlServingRestLlmInferenceServiceFactory implements LlmInferenceServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DjlServingRestLlmInferenceServiceFactory.class);

    @Override
    public LlmInferenceService create(Map<String, Object> config) throws LlmInferenceException {
        logger.info("Creating DjlServingRestLlmInferenceService");
        return new DjlServingRestLlmInferenceService(config);
    }

    @Override
    public String getFactoryName() {
        return "djl-serving-rest";
    }

    @Override
    public String getDescription() {
        return "REST client for DJL Serving backend";
    }
}
