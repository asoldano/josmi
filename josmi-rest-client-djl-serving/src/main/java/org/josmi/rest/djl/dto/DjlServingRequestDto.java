package org.josmi.rest.djl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.CompletionRequest;
import org.josmi.api.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO for requests to the DJL Serving backend.
 * DJL Serving uses a different format than the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DjlServingRequestDto {

    @JsonProperty("data")
    private String data;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    /**
     * Default constructor.
     */
    public DjlServingRequestDto() {
        this.parameters = new HashMap<>();
    }

    /**
     * Constructs a new DjlServingRequestDto with the specified data and parameters.
     *
     * @param data the input data
     * @param parameters the parameters
     */
    public DjlServingRequestDto(String data, Map<String, Object> parameters) {
        this.data = data;
        this.parameters = parameters;
    }

    /**
     * Gets the input data.
     *
     * @return the input data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the input data.
     *
     * @param data the input data
     */
    public void setData(String data) {
        this.data = data;
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
     * Converts a ChatRequest to a DjlServingRequestDto.
     *
     * @param request the ChatRequest to convert
     * @return a new DjlServingRequestDto
     */
    public static DjlServingRequestDto fromChatRequest(ChatRequest request) {
        if (request == null) {
            return null;
        }
        
        // Format chat messages into a prompt string
        String prompt = formatChatMessages(request.getMessages());
        
        // Copy parameters
        Map<String, Object> parameters = new HashMap<>(request.getParameters());
        
        return new DjlServingRequestDto(prompt, parameters);
    }

    /**
     * Converts a CompletionRequest to a DjlServingRequestDto.
     *
     * @param request the CompletionRequest to convert
     * @return a new DjlServingRequestDto
     */
    public static DjlServingRequestDto fromCompletionRequest(CompletionRequest request) {
        if (request == null) {
            return null;
        }
        
        // Copy parameters
        Map<String, Object> parameters = new HashMap<>(request.getParameters());
        
        return new DjlServingRequestDto(request.getPrompt(), parameters);
    }

    /**
     * Formats chat messages into a prompt string for DJL Serving.
     *
     * @param messages the list of chat messages
     * @return a formatted prompt string
     */
    private static String formatChatMessages(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        StringBuilder prompt = new StringBuilder();
        
        for (Message message : messages) {
            String role = message.getRole();
            String content = message.getContent();
            
            if (role == null || content == null) {
                continue;
            }
            
            // Format based on role
            switch (role.toLowerCase()) {
                case "system":
                    prompt.append("<|system|>\n").append(content).append("\n");
                    break;
                case "user":
                    prompt.append("<|user|>\n").append(content).append("\n");
                    break;
                case "assistant":
                    prompt.append("<|assistant|>\n").append(content).append("\n");
                    break;
                default:
                    prompt.append(content).append("\n");
                    break;
            }
        }
        
        // Add assistant prefix for the response
        prompt.append("<|assistant|>\n");
        
        return prompt.toString();
    }
}
