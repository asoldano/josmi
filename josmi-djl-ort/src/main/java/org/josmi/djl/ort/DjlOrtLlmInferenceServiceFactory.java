package org.josmi.djl.ort;

import org.josmi.api.LlmInferenceException;
import org.josmi.api.LlmInferenceService;
import org.josmi.api.LlmInferenceServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Factory for creating DjlOrtLlmInferenceService instances.
 */
public class DjlOrtLlmInferenceServiceFactory implements LlmInferenceServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(DjlOrtLlmInferenceServiceFactory.class);

    @Override
    public LlmInferenceService create(Map<String, Object> config) throws LlmInferenceException {
        logger.info("Creating DjlOrtLlmInferenceService");
        return new DjlOrtLlmInferenceService(config);
    }

    @Override
    public String getFactoryName() {
        return "djl-ort";
    }

    @Override
    public String getDescription() {
        return "Deep Java Library with ONNX Runtime implementation for LLM inference";
    }
}
