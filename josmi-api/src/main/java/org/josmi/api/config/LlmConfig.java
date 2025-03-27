package org.josmi.api.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class for LLM inference services.
 * Provides common configuration parameters and utility methods.
 */
public class LlmConfig {

    // Common configuration keys
    public static final String MODEL_PATH = "model.path";
    public static final String MODEL_URL = "model.url";
    public static final String MODEL_ID = "model.id";
    public static final String TOKENIZER_PATH = "tokenizer.path";
    public static final String TOKENIZER_URL = "tokenizer.url";
    public static final String TOKENIZER_ID = "tokenizer.id";
    public static final String MAX_TOKENS = "max.tokens";
    public static final String TEMPERATURE = "temperature";
    public static final String TOP_P = "top.p";
    public static final String TOP_K = "top.k";
    public static final String REPETITION_PENALTY = "repetition.penalty";
    public static final String PRESENCE_PENALTY = "presence.penalty";
    public static final String FREQUENCY_PENALTY = "frequency.penalty";
    public static final String SEED = "seed";
    public static final String THREADS = "threads";
    public static final String DEVICE = "device";
    public static final String ENDPOINT_URL = "endpoint.url";
    public static final String API_KEY = "api.key";
    public static final String TIMEOUT_MS = "timeout.ms";

    private final Map<String, Object> configMap;

    /**
     * Constructs a new LlmConfig with an empty configuration map.
     */
    public LlmConfig() {
        this.configMap = new HashMap<>();
    }

    /**
     * Constructs a new LlmConfig with the specified configuration map.
     *
     * @param configMap the configuration map
     */
    public LlmConfig(Map<String, Object> configMap) {
        this.configMap = new HashMap<>(configMap);
    }

    /**
     * Gets the configuration map.
     *
     * @return the configuration map
     */
    public Map<String, Object> getConfigMap() {
        return new HashMap<>(configMap);
    }

    /**
     * Sets a configuration value.
     *
     * @param key the configuration key
     * @param value the configuration value
     * @return this LlmConfig instance for method chaining
     */
    public LlmConfig set(String key, Object value) {
        configMap.put(key, value);
        return this;
    }

    /**
     * Gets a configuration value.
     *
     * @param key the configuration key
     * @return the configuration value, or null if not found
     */
    public Object get(String key) {
        return configMap.get(key);
    }

    /**
     * Gets a configuration value as a string.
     *
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found
     * @return the configuration value as a string, or the default value if not found
     */
    public String getString(String key, String defaultValue) {
        Object value = configMap.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Gets a configuration value as an integer.
     *
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found or not an integer
     * @return the configuration value as an integer, or the default value if not found or not an integer
     */
    public int getInt(String key, int defaultValue) {
        Object value = configMap.get(key);
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
    public double getDouble(String key, double defaultValue) {
        Object value = configMap.get(key);
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
    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = configMap.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return defaultValue;
    }

    /**
     * Builder for creating LlmConfig instances.
     */
    public static class Builder {
        private final Map<String, Object> configMap = new HashMap<>();

        /**
         * Sets a configuration value.
         *
         * @param key the configuration key
         * @param value the configuration value
         * @return this Builder instance for method chaining
         */
        public Builder set(String key, Object value) {
            configMap.put(key, value);
            return this;
        }

        /**
         * Sets the model path.
         *
         * @param modelPath the path to the model file
         * @return this Builder instance for method chaining
         */
        public Builder modelPath(String modelPath) {
            return set(MODEL_PATH, modelPath);
        }

        /**
         * Sets the model URL.
         *
         * @param modelUrl the URL to the model
         * @return this Builder instance for method chaining
         */
        public Builder modelUrl(String modelUrl) {
            return set(MODEL_URL, modelUrl);
        }

        /**
         * Sets the model ID.
         *
         * @param modelId the ID of the model
         * @return this Builder instance for method chaining
         */
        public Builder modelId(String modelId) {
            return set(MODEL_ID, modelId);
        }

        /**
         * Sets the tokenizer path.
         *
         * @param tokenizerPath the path to the tokenizer file
         * @return this Builder instance for method chaining
         */
        public Builder tokenizerPath(String tokenizerPath) {
            return set(TOKENIZER_PATH, tokenizerPath);
        }

        /**
         * Sets the tokenizer URL.
         *
         * @param tokenizerUrl the URL to the tokenizer
         * @return this Builder instance for method chaining
         */
        public Builder tokenizerUrl(String tokenizerUrl) {
            return set(TOKENIZER_URL, tokenizerUrl);
        }

        /**
         * Sets the tokenizer ID.
         *
         * @param tokenizerId the ID of the tokenizer
         * @return this Builder instance for method chaining
         */
        public Builder tokenizerId(String tokenizerId) {
            return set(TOKENIZER_ID, tokenizerId);
        }

        /**
         * Sets the maximum number of tokens to generate.
         *
         * @param maxTokens the maximum number of tokens
         * @return this Builder instance for method chaining
         */
        public Builder maxTokens(int maxTokens) {
            return set(MAX_TOKENS, maxTokens);
        }

        /**
         * Sets the temperature for sampling.
         *
         * @param temperature the temperature value
         * @return this Builder instance for method chaining
         */
        public Builder temperature(double temperature) {
            return set(TEMPERATURE, temperature);
        }

        /**
         * Sets the top-p value for nucleus sampling.
         *
         * @param topP the top-p value
         * @return this Builder instance for method chaining
         */
        public Builder topP(double topP) {
            return set(TOP_P, topP);
        }

        /**
         * Sets the top-k value for top-k sampling.
         *
         * @param topK the top-k value
         * @return this Builder instance for method chaining
         */
        public Builder topK(int topK) {
            return set(TOP_K, topK);
        }

        /**
         * Sets the repetition penalty.
         *
         * @param repetitionPenalty the repetition penalty value
         * @return this Builder instance for method chaining
         */
        public Builder repetitionPenalty(double repetitionPenalty) {
            return set(REPETITION_PENALTY, repetitionPenalty);
        }

        /**
         * Sets the presence penalty.
         *
         * @param presencePenalty the presence penalty value
         * @return this Builder instance for method chaining
         */
        public Builder presencePenalty(double presencePenalty) {
            return set(PRESENCE_PENALTY, presencePenalty);
        }

        /**
         * Sets the frequency penalty.
         *
         * @param frequencyPenalty the frequency penalty value
         * @return this Builder instance for method chaining
         */
        public Builder frequencyPenalty(double frequencyPenalty) {
            return set(FREQUENCY_PENALTY, frequencyPenalty);
        }

        /**
         * Sets the random seed.
         *
         * @param seed the random seed
         * @return this Builder instance for method chaining
         */
        public Builder seed(long seed) {
            return set(SEED, seed);
        }

        /**
         * Sets the number of threads to use.
         *
         * @param threads the number of threads
         * @return this Builder instance for method chaining
         */
        public Builder threads(int threads) {
            return set(THREADS, threads);
        }

        /**
         * Sets the device to use (e.g., "CPU", "CUDA", "DirectML").
         *
         * @param device the device name
         * @return this Builder instance for method chaining
         */
        public Builder device(String device) {
            return set(DEVICE, device);
        }

        /**
         * Sets the endpoint URL for REST clients.
         *
         * @param endpointUrl the endpoint URL
         * @return this Builder instance for method chaining
         */
        public Builder endpointUrl(String endpointUrl) {
            return set(ENDPOINT_URL, endpointUrl);
        }

        /**
         * Sets the API key for authenticated services.
         *
         * @param apiKey the API key
         * @return this Builder instance for method chaining
         */
        public Builder apiKey(String apiKey) {
            return set(API_KEY, apiKey);
        }

        /**
         * Sets the timeout in milliseconds.
         *
         * @param timeoutMs the timeout in milliseconds
         * @return this Builder instance for method chaining
         */
        public Builder timeoutMs(int timeoutMs) {
            return set(TIMEOUT_MS, timeoutMs);
        }

        /**
         * Builds a new LlmConfig instance.
         *
         * @return a new LlmConfig instance
         */
        public LlmConfig build() {
            return new LlmConfig(configMap);
        }
    }

    /**
     * Creates a new builder for LlmConfig.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
