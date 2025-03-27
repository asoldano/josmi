package org.josmi.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a response from an LLM completion request.
 */
public class CompletionResponse {
    private String text;
    private Map<String, Object> metadata;

    public CompletionResponse() {
        this.metadata = new HashMap<>();
    }

    public CompletionResponse(String text, Map<String, Object> metadata) {
        this.text = text;
        this.metadata = metadata;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompletionResponse that = (CompletionResponse) o;
        return Objects.equals(text, that.text) && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, metadata);
    }

    @Override
    public String toString() {
        return "CompletionResponse{" +
                "text='" + text + '\'' +
                ", metadata=" + metadata +
                '}';
    }

    /**
     * Builder for creating CompletionResponse instances.
     */
    public static class Builder {
        private String text;
        private final Map<String, Object> metadata = new HashMap<>();

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder addMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder setMetadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder totalTokens(int totalTokens) {
            this.metadata.put("total_tokens", totalTokens);
            return this;
        }

        public Builder promptTokens(int promptTokens) {
            this.metadata.put("prompt_tokens", promptTokens);
            return this;
        }

        public Builder completionTokens(int completionTokens) {
            this.metadata.put("completion_tokens", completionTokens);
            return this;
        }

        public Builder latencyMs(long latencyMs) {
            this.metadata.put("latency_ms", latencyMs);
            return this;
        }

        public CompletionResponse build() {
            return new CompletionResponse(text, new HashMap<>(metadata));
        }
    }

    /**
     * Creates a new builder for CompletionResponse.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
