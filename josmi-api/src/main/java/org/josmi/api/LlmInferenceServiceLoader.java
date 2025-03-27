package org.josmi.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Utility class for loading LlmInferenceServiceFactory implementations.
 * Uses Java's ServiceLoader mechanism to discover implementations at runtime.
 */
public class LlmInferenceServiceLoader {

    private static final ServiceLoader<LlmInferenceServiceFactory> serviceLoader = 
            ServiceLoader.load(LlmInferenceServiceFactory.class);

    /**
     * Gets all available LlmInferenceServiceFactory implementations.
     *
     * @return a list of all available factory implementations
     */
    public static List<LlmInferenceServiceFactory> getFactories() {
        List<LlmInferenceServiceFactory> factories = new ArrayList<>();
        Iterator<LlmInferenceServiceFactory> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            factories.add(iterator.next());
        }
        return factories;
    }

    /**
     * Gets a LlmInferenceServiceFactory by name.
     *
     * @param name the name of the factory to get
     * @return the factory with the specified name, or null if not found
     */
    public static LlmInferenceServiceFactory getFactory(String name) {
        for (LlmInferenceServiceFactory factory : getFactories()) {
            if (factory.getFactoryName().equals(name)) {
                return factory;
            }
        }
        return null;
    }

    /**
     * Creates a new LlmInferenceService instance using the specified factory name and configuration.
     *
     * @param factoryName the name of the factory to use
     * @param config the configuration parameters for the service
     * @return a new LlmInferenceService instance
     * @throws LlmInferenceException if the service cannot be created
     */
    public static LlmInferenceService createService(String factoryName, Map<String, Object> config) 
            throws LlmInferenceException {
        LlmInferenceServiceFactory factory = getFactory(factoryName);
        if (factory == null) {
            throw new LlmInferenceException("No factory found with name: " + factoryName);
        }
        return factory.create(config);
    }
}
