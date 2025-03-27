package org.josmi.api.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a request to chat with an LLM.
 */
public class ChatRequest {
    private List<Message> messages;
    private Map<String, Object> parameters;

    public ChatRequest() {
        this.messages = new ArrayList<>();
        this.parameters = new HashMap<>();
    }

    public ChatRequest(List<Message> messages, Map<String, Object> parameters) {
        this.messages = messages;
        this.parameters = parameters;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
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
        ChatRequest that = (ChatRequest) o;
        return Objects.equals(messages, that.messages) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messages, parameters);
    }

    @Override
    public String toString() {
        return "ChatRequest{" +
                "messages=" + messages +
                ", parameters=" + parameters +
                '}';
    }

    /**
     * Builder for creating ChatRequest instances.
     */
    public static class Builder {
        private final List<Message> messages = new ArrayList<>();
        private final Map<String, Object> parameters = new HashMap<>();

        public Builder addMessage(Message message) {
            this.messages.add(message);
            return this;
        }

        public Builder addMessages(List<Message> messages) {
            this.messages.addAll(messages);
            return this;
        }

        public Builder addSystemMessage(String content) {
            this.messages.add(Message.system(content));
            return this;
        }

        public Builder addUserMessage(String content) {
            this.messages.add(Message.user(content));
            return this;
        }

        public Builder addAssistantMessage(String content) {
            this.messages.add(Message.assistant(content));
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

        public ChatRequest build() {
            return new ChatRequest(new ArrayList<>(messages), new HashMap<>(parameters));
        }
    }

    /**
     * Creates a new builder for ChatRequest.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}
