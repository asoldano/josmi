package org.josmi.rest.djl.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.josmi.rest.djl.dto.DjlServingRequestDto;
import org.josmi.rest.djl.dto.DjlServingResponseDto;

import java.util.Map;

/**
 * REST client interface for the DJL Serving backend.
 */
public interface DjlServingRestClient {

    /**
     * Sends a request to the DJL Serving backend.
     * This is used for both chat and completion requests.
     *
     * @param request the request
     * @return the response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    DjlServingResponseDto predict(DjlServingRequestDto request);

    /**
     * Checks if the backend is ready.
     *
     * @return a map containing the health status
     */
    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> ping();
}
