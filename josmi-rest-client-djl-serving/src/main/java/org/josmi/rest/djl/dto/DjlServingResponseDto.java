package org.josmi.rest.djl.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.CompletionResponse;
import org.josmi.api.model.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for responses from the DJL Serving backend.
 * DJL Serving uses a different format than the Python FastAPI backend.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DjlServingResponseDto {

    @JsonProperty("data")
    private String data;

    @JsonProperty("metrics")
    private Map<String, Object> metrics;

    /**
     * Default constructor.
     */
    public DjlServingResponseDto() {
        this.metrics = new HashMap<>();
    }

    /**
     * Constructs a new DjlServingResponseDto with the specified data and metrics.
     *
     * @param data the output data
     * @param metrics the metrics
     */
    public DjlServingResponseDto(String data, Map<String, Object> metrics) {
        this.data = data;
        this.metrics = metrics;
    }

    /**
     * Gets the output data.
     *
     * @return the output data
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the output data.
     *
     * @param data the output data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the metrics.
     *
     * @return the metrics
     */
    public Map<String, Object> getMetrics() {
        return metrics;
    }

    /**
     * Sets the metrics.
     *
     * @param metrics the metrics
     */
    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

    /**
     * Converts a DjlServingResponseDto to a ChatResponse.
     *
     * @return a new ChatResponse
     */
    public ChatResponse toChatResponse() {
        // Create response message
        Message responseMessage = Message.assistant(data);
        
        // Convert metrics to metadata
        Map<String, Object> metadata = new HashMap<>();
        if (metrics != null) {
            metadata.putAll(metrics);
        }
        
        return new ChatResponse(responseMessage, metadata);
    }

    /**
     * Converts a DjlServingResponseDto to a CompletionResponse.
     *
     * @return a new CompletionResponse
     */
    public CompletionResponse toCompletionResponse() {
        // Convert metrics to metadata
        Map<String, Object> metadata = new HashMap<>();
        if (metrics != null) {
            metadata.putAll(metrics);
        }
        
        return new CompletionResponse(data, metadata);
    }
}
