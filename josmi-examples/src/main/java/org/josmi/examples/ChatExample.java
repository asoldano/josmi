package org.josmi.examples;

import org.josmi.api.LlmInferenceException;
import org.josmi.api.LlmInferenceService;
import org.josmi.api.LlmInferenceServiceLoader;
import org.josmi.api.config.LlmConfig;
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Example of using JOSMI for chat with an ONNX model.
 * This example demonstrates how to use all four modules.
 */
public class ChatExample {

    private static final Logger logger = LoggerFactory.getLogger(ChatExample.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: ChatExample <implementation> <model-path> [endpoint-url]");
            System.out.println("  implementation: ort, djl-ort, python-rest, djl-serving-rest");
            System.out.println("  model-path: path to the ONNX model file");
            System.out.println("  endpoint-url: URL of the REST endpoint (required for python-rest and djl-serving-rest)");
            return;
        }

        String implementation = args[0];
        String modelPath = args[1];
        String endpointUrl = args.length > 2 ? args[2] : null;

        try {
            // Create the appropriate service
            LlmInferenceService service = createService(implementation, modelPath, endpointUrl);
            
            if (service == null) {
                System.err.println("Failed to create service for implementation: " + implementation);
                return;
            }

            // Check if the service is ready
            if (!service.isReady()) {
                System.err.println("Service is not ready");
                return;
            }

            System.out.println("Chat with the model. Type 'exit' to quit.");
            System.out.println("Using implementation: " + implementation);

            // Create a list to store the conversation history
            List<Message> messages = new ArrayList<>();
            
            // Add a system message to set the behavior of the assistant
            messages.add(Message.system("You are a helpful assistant."));

            // Create a scanner for user input
            Scanner scanner = new Scanner(System.in);

            while (true) {
                // Get user input
                System.out.print("User: ");
                String userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                // Add user message to the conversation
                messages.add(Message.user(userInput));

                // Create a chat request
                ChatRequest request = ChatRequest.builder()
                        .addMessages(messages)
                        .temperature(0.7)
                        .maxTokens(1024)
                        .build();

                // Get a response from the model
                ChatResponse response = service.chat(request);

                // Print the response
                System.out.println("Assistant: " + response.getContent());

                // Add the assistant's response to the conversation history
                messages.add(response.getResponse());

                // Print metadata
                System.out.println("Metadata: " + response.getMetadata());
            }

            // Close the scanner when done
            scanner.close();
            
            // Close the service when done
            service.close();

        } catch (Exception e) {
            logger.error("Error in chat example", e);
            e.printStackTrace();
        }
    }

    /**
     * Creates an LlmInferenceService based on the specified implementation.
     *
     * @param implementation the implementation to use
     * @param modelPath the path to the ONNX model file
     * @param endpointUrl the URL of the REST endpoint (for REST implementations)
     * @return a new LlmInferenceService
     * @throws LlmInferenceException if the service cannot be created
     */
    private static LlmInferenceService createService(String implementation, String modelPath, String endpointUrl) 
            throws LlmInferenceException {
        
        switch (implementation) {
            case "ort":
                // Create a configuration for ONNX Runtime
                LlmConfig ortConfig = LlmConfig.builder()
                        .modelPath(modelPath)
                        .build();
                
                // Create a service using the ONNX Runtime implementation
                return LlmInferenceServiceLoader.createService("ort", ortConfig.getConfigMap());
                
            case "djl-ort":
                // Create a configuration for DJL with ONNX Runtime
                LlmConfig djlConfig = LlmConfig.builder()
                        .modelPath(modelPath)
                        .build();
                
                // Create a service using the DJL with ONNX Runtime implementation
                return LlmInferenceServiceLoader.createService("djl-ort", djlConfig.getConfigMap());
                
            case "python-rest":
                if (endpointUrl == null) {
                    System.err.println("Endpoint URL is required for python-rest implementation");
                    return null;
                }
                
                // Create a configuration for Python REST client
                LlmConfig pythonRestConfig = LlmConfig.builder()
                        .endpointUrl(endpointUrl)
                        .timeoutMs(30000)
                        .build();
                
                // Create a service using the Python REST client implementation
                return LlmInferenceServiceLoader.createService("python-rest", pythonRestConfig.getConfigMap());
                
            case "djl-serving-rest":
                if (endpointUrl == null) {
                    System.err.println("Endpoint URL is required for djl-serving-rest implementation");
                    return null;
                }
                
                // Create a configuration for DJL Serving REST client
                LlmConfig djlServingRestConfig = LlmConfig.builder()
                        .endpointUrl(endpointUrl)
                        .timeoutMs(30000)
                        .build();
                
                // Create a service using the DJL Serving REST client implementation
                return LlmInferenceServiceLoader.createService("djl-serving-rest", djlServingRestConfig.getConfigMap());
                
            default:
                System.err.println("Unknown implementation: " + implementation);
                return null;
        }
    }
}
