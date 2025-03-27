package org.josmi.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a request for text completion from an LLM.
 */
public class CompletionRequest {
    private String prompt;
    private Map<String, Object> parameters;

    public CompletionRequest() {
        this.parameters = new HashMap<>();
    }

    public CompletionRequest(String prompt, Map<String, Object> parameters) {
        this.prompt = prompt;
        this.parameters = parameters;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletionRequest that = (CompletionRequest) o;
        return Objects.equals(prompt, that.prompt) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prompt, parameters);
    }

    @Override
    public String toString() {
        return "CompletionRequest{" +
                "prompt='" + prompt + '\'' +
                ", parameters=" + parameters +
                '}';
    }

    /**
     * Builder for creating CompletionRequest instances.
     */
    public static class Builder {
        private String prompt;
        private final Map<String, Object> parameters = new HashMap<>();

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder setParameter(String key, Object value) {
            this.parameters.put(key, value);
            return this;
        }

        public Builder setParameters(Map<String, Object> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }

        public Builder temperature(double temperature) {
            this.parameters.put("temperature", temperature);
            return this;
        }

        public Builder topP(double topP) {
            this.parameters.put("top_p", topP);
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.parameters.put("max_tokens", maxTokens);
            return this;
        }

        public CompletionRequest build() {
            return new CompletionRequest(prompt, new HashMap<>(parameters));
        }
    }

    /**
     * Creates a new builder for CompletionRequest.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
