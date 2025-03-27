package org.josmi.rest.python;

import org.josmi.api.LlmInferenceException;
import org.josmi.api.LlmInferenceService;
import org.josmi.api.LlmInferenceServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Factory for creating PythonRestLlmInferenceService instances.
 */
public class PythonRestLlmInferenceServiceFactory implements LlmInferenceServiceFactory {

    private static final Logger logger = LoggerFactory.getLogger(PythonRestLlmInferenceServiceFactory.class);

    @Override
    public LlmInferenceService create(Map<String, Object> config) throws LlmInferenceException {
        logger.info("Creating PythonRestLlmInferenceService");
        return new PythonRestLlmInferenceService(config);
    }

    @Override
    public String getFactoryName() {
        return "python-rest";
    }

    @Override
    public String getDescription() {
        return "REST client for Python FastAPI + ONNX Runtime backend";
    }
}
