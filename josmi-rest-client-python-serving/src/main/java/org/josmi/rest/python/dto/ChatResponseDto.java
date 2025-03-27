package org.josmi.rest.python.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for chat response objects in the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponseDto {

    @JsonProperty("response")
    private MessageDto response;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Default constructor.
     */
    public ChatResponseDto() {
        this.metadata = new HashMap<>();
    }

    /**
     * Constructs a new ChatResponseDto with the specified response and metadata.
     *
     * @param response the response message
     * @param metadata the metadata
     */
    public ChatResponseDto(MessageDto response, Map<String, Object> metadata) {
        this.response = response;
        this.metadata = metadata;
    }

    /**
     * Gets the response message.
     *
     * @return the response message
     */
    public MessageDto getResponse() {
        return response;
    }

    /**
     * Sets the response message.
     *
     * @param response the response message
     */
    public void setResponse(MessageDto response) {
        this.response = response;
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Converts a ChatResponse to a ChatResponseDto.
     *
     * @param response the ChatResponse to convert
     * @return a new ChatResponseDto
     */
    public static ChatResponseDto fromChatResponse(ChatResponse response) {
        if (response == null) {
            return null;
        }
        
        MessageDto messageDto = MessageDto.fromMessage(response.getResponse());
        
        return new ChatResponseDto(messageDto, new HashMap<>(response.getMetadata()));
    }

    /**
     * Converts a ChatResponseDto to a ChatResponse.
     *
     * @return a new ChatResponse
     */
    public ChatResponse toChatResponse() {
        Message message = response != null ? response.toMessage() : null;
        
        return new ChatResponse(message, new HashMap<>(metadata));
    }
}
