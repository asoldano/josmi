package org.josmi.rest.python.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.CompletionRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for completion request objects in the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionRequestDto {

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    /**
     * Default constructor.
     */
    public CompletionRequestDto() {
        this.parameters = new HashMap<>();
    }

    /**
     * Constructs a new CompletionRequestDto with the specified prompt and parameters.
     *
     * @param prompt the prompt
     * @param parameters the parameters
     */
    public CompletionRequestDto(String prompt, Map<String, Object> parameters) {
        this.prompt = prompt;
        this.parameters = parameters;
    }

    /**
     * Gets the prompt.
     *
     * @return the prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Sets the prompt.
     *
     * @param prompt the prompt
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
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
     * Converts a CompletionRequest to a CompletionRequestDto.
     *
     * @param request the CompletionRequest to convert
     * @return a new CompletionRequestDto
     */
    public static CompletionRequestDto fromCompletionRequest(CompletionRequest request) {
        if (request == null) {
            return null;
        }
        
        return new CompletionRequestDto(request.getPrompt(), new HashMap<>(request.getParameters()));
    }

    /**
     * Converts a CompletionRequestDto to a CompletionRequest.
     *
     * @return a new CompletionRequest
     */
    public CompletionRequest toCompletionRequest() {
        return new CompletionRequest(prompt, new HashMap<>(parameters));
    }
}
