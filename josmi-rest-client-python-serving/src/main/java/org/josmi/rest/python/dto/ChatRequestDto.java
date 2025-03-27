package org.josmi.rest.python.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO for chat request objects in the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRequestDto {

    @JsonProperty("messages")
    private List<MessageDto> messages;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    /**
     * Default constructor.
     */
    public ChatRequestDto() {
        this.messages = new ArrayList<>();
        this.parameters = new HashMap<>();
    }

    /**
     * Constructs a new ChatRequestDto with the specified messages and parameters.
     *
     * @param messages the list of messages
     * @param parameters the parameters
     */
    public ChatRequestDto(List<MessageDto> messages, Map<String, Object> parameters) {
        this.messages = messages;
        this.parameters = parameters;
    }

    /**
     * Gets the list of messages.
     *
     * @return the list of messages
     */
    public List<MessageDto> getMessages() {
        return messages;
    }

    /**
     * Sets the list of messages.
     *
     * @param messages the list of messages
     */
    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters.
     *
     * @param parameters the parameters
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Converts a ChatRequest to a ChatRequestDto.
     *
     * @param request the ChatRequest to convert
     * @return a new ChatRequestDto
     */
    public static ChatRequestDto fromChatRequest(ChatRequest request) {
        if (request == null) {
            return null;
        }
        
        List<MessageDto> messageDtos = request.getMessages().stream()
                .map(MessageDto::fromMessage)
                .collect(Collectors.toList());
        
        return new ChatRequestDto(messageDtos, new HashMap<>(request.getParameters()));
    }

    /**
     * Converts a ChatRequestDto to a ChatRequest.
     *
     * @return a new ChatRequest
     */
    public ChatRequest toChatRequest() {
        List<Message> messageList = messages.stream()
                .map(MessageDto::toMessage)
                .collect(Collectors.toList());
        
        return new ChatRequest(messageList, new HashMap<>(parameters));
    }
}
