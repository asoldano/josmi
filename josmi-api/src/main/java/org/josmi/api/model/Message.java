package org.josmi.api.model;

import java.util.Objects;

/**
 * Represents a message in a conversation with an LLM.
 */
public class Message {
    private String role;
    private String content;

    public Message() {
    }

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(role, message.role) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, content);
    }

    @Override
    public String toString() {
        return "Message{" +
                "role='" + role + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    /**
     * Builder for creating Message instances.
     */
    public static class Builder {
        private String role;
        private String content;

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Message build() {
            return new Message(role, content);
        }
    }

    /**
     * Creates a new builder for Message.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a system message.
     *
     * @param content the message content
     * @return a new Message with role "system"
     */
    public static Message system(String content) {
        return new Message("system", content);
    }

    /**
     * Creates a user message.
     *
     * @param content the message content
     * @return a new Message with role "user"
     */
    public static Message user(String content) {
        return new Message("user", content);
    }

    /**
     * Creates an assistant message.
     *
     * @param content the message content
     * @return a new Message with role "assistant"
     */
    public static Message assistant(String content) {
        return new Message("assistant", content);
    }
}
