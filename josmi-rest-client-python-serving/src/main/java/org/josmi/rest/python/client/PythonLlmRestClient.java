package org.josmi.rest.python.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.josmi.rest.python.dto.ChatRequestDto;
import org.josmi.rest.python.dto.ChatResponseDto;
import org.josmi.rest.python.dto.CompletionRequestDto;
import org.josmi.rest.python.dto.CompletionResponseDto;

/**
 * REST client interface for the Python FastAPI backend.
 */
@Path("/v1")
public interface PythonLlmRestClient {

    /**
     * Sends a chat request to the backend.
     *
     * @param request the chat request
     * @return the chat response
     */
    @POST
    @Path("/chat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ChatResponseDto chat(ChatRequestDto request);

    /**
     * Sends a completion request to the backend.
     *
     * @param request the completion request
     * @return the completion response
     */
    @POST
    @Path("/completion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    CompletionResponseDto complete(CompletionRequestDto request);

    /**
     * Checks if the backend is ready.
     *
     * @return true if the backend is ready, false otherwise
     */
    @POST
    @Path("/health")
    @Produces(MediaType.APPLICATION_JSON)
    boolean isReady();
}
