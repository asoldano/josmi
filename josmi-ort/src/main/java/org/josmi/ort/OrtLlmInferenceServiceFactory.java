package org.josmi.ort;

import org.josmi.api.LlmInferenceException;
import org.josmi.api.LlmInferenceService;
import org.josmi.api.LlmInferenceServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Factory for creating OrtLlmInferenceService instances.
 */
public class OrtLlmInferenceServiceFactory implements LlmInferenceServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(OrtLlmInferenceServiceFactory.class);

    @Override
    public LlmInferenceService create(Map<String, Object> config) throws LlmInferenceException {
        logger.info("Creating OrtLlmInferenceService");
        return new OrtLlmInferenceService(config);
    }

    @Override
    public String getFactoryName() {
        return "ort";
    }

    @Override
    public String getDescription() {
        return "ONNX Runtime implementation for LLM inference";
    }
}
