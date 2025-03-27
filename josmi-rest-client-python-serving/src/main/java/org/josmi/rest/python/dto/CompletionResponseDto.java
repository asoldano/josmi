package org.josmi.rest.python.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.CompletionResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for completion response objects in the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletionResponseDto {

    @JsonProperty("text")
    private String text;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Default constructor.
     */
    public CompletionResponseDto() {
        this.metadata = new HashMap<>();
    }

    /**
     * Constructs a new CompletionResponseDto with the specified text and metadata.
     *
     * @param text the generated text
     * @param metadata the metadata
     */
    public CompletionResponseDto(String text, Map<String, Object> metadata) {
        this.text = text;
        this.metadata = metadata;
    }

    /**
     * Gets the generated text.
     *
     * @return the generated text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the generated text.
     *
     * @param text the generated text
     */
    public void setText(String text) {
        this.text = text;
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
     * Converts a CompletionResponse to a CompletionResponseDto.
     *
     * @param response the CompletionResponse to convert
     * @return a new CompletionResponseDto
     */
    public static CompletionResponseDto fromCompletionResponse(CompletionResponse response) {
        if (response == null) {
            return null;
        }
        
        return new CompletionResponseDto(response.getText(), new HashMap<>(response.getMetadata()));
    }

    /**
     * Converts a CompletionResponseDto to a CompletionResponse.
     *
     * @return a new CompletionResponse
     */
    public CompletionResponse toCompletionResponse() {
        return new CompletionResponse(text, new HashMap<>(metadata));
    }
}
