package org.josmi.rest.python.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.Message;

/**
 * DTO for message objects in the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

    @JsonProperty("role")
    private String role;

    @JsonProperty("content")
    private String content;

    /**
     * Default constructor.
     */
    public MessageDto() {
    }

    /**
     * Constructs a new MessageDto with the specified role and content.
     *
     * @param role the role of the message
     * @param content the content of the message
     */
    public MessageDto(String role, String content) {
        this.role = role;
        this.content = content;
    }

    /**
     * Gets the role of the message.
     *
     * @return the role of the message
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the message.
     *
     * @param role the role of the message
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the content of the message.
     *
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     *
     * @param content the content of the message
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Converts a Message to a MessageDto.
     *
     * @param message the Message to convert
     * @return a new MessageDto
     */
    public static MessageDto fromMessage(Message message) {
        if (message == null) {
            return null;
        }
        return new MessageDto(message.getRole(), message.getContent());
    }

    /**
     * Converts a MessageDto to a Message.
     *
     * @return a new Message
     */
    public Message toMessage() {
        return new Message(role, content);
    }
}
