package org.josmi.api.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a response from an LLM chat request.
 */
public class ChatResponse {
    private Message response;
    private Map<String, Object> metadata;

    public ChatResponse() {
        this.metadata = new HashMap<>();
    }

    public ChatResponse(Message response, Map<String, Object> metadata) {
        this.response = response;
        this.metadata = metadata;
    }

    public Message getResponse() {
        return response;
    }

    public void setResponse(Message response) {
        this.response = response;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the content of the response message.
     *
     * @return the content of the response message
     */
    public String getContent() {
        return response != null ? response.getContent() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponse that = (ChatResponse) o;
        return Objects.equals(response, that.response) && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response, metadata);
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "response=" + response +
                ", metadata=" + metadata +
                '}';
    }

    /**
     * Builder for creating ChatResponse instances.
     */
    public static class Builder {
        private Message response;
        private final Map<String, Object> metadata = new HashMap<>();

        public Builder response(Message response) {
            this.response = response;
            return this;
        }

        public Builder assistantResponse(String content) {
            this.response = Message.assistant(content);
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

        public ChatResponse build() {
            return new ChatResponse(response, new HashMap<>(metadata));
        }
    }

    /**
     * Creates a new builder for ChatResponse.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
