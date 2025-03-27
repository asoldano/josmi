package org.josmi.ort;

import org.josmi.api.LlmInferenceException;
import org.josmi.api.LlmInferenceService;
import org.josmi.api.config.LlmConfig;
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.Disabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for OrtLlmInferenceService using a real ONNX model.
 * 
 * This test demonstrates how to use OrtLlmInferenceService for a chat interaction.
 * It uses the TinyLlama ONNX model from Hugging Face.
 * 
 * Note: This test requires downloading a model from Hugging Face, which may take some time.
 * To skip this test, run Maven with -DskipIntegrationTests=true
 * 
 * IMPORTANT: To run this test, you need to have the ONNX Runtime native libraries in your Java library path.
 * You can run the test with the following command:
 * 
 * mvn test -Dtest=OrtLlmInferenceServiceTest -DskipIntegrationTests=false -Djava.library.path=/path/to/onnxruntime/lib
 * 
 * You can download the ONNX Runtime from https://github.com/microsoft/onnxruntime/releases
 */
public class OrtLlmInferenceServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(OrtLlmInferenceServiceTest.class);
    
    // Model information
    private static final String MODEL_URL = "https://huggingface.co/llmware/tiny-llama-chat-onnx/resolve/main/model.onnx";
    private static final String MODEL_DATA_URL = "https://huggingface.co/llmware/tiny-llama-chat-onnx/resolve/main/model.onnx.data";
    private static final String MODEL_FILENAME = "model.onnx";
    private static final String MODEL_DATA_FILENAME = "model.onnx.data";
    private static final String TEST_MODELS_DIR = "target/test-models";
    
    private LlmInferenceService service;
    private String modelPath;
    
    @BeforeEach
    public void setUp() throws Exception {
        // Create test models directory if it doesn't exist
        File modelsDir = new File(TEST_MODELS_DIR);
        if (!modelsDir.exists()) {
            modelsDir.mkdirs();
        }
        
        // Set the model path
        modelPath = TEST_MODELS_DIR + "/" + MODEL_FILENAME;
        String modelDataPath = TEST_MODELS_DIR + "/" + MODEL_DATA_FILENAME;
        
        // Download the model files if they don't exist
        File modelFile = new File(modelPath);
        File modelDataFile = new File(modelDataPath);
        
        if (!modelFile.exists()) {
            downloadFile(MODEL_URL, modelPath);
        }
        
        if (!modelDataFile.exists()) {
            downloadFile(MODEL_DATA_URL, modelDataPath);
        }
        
        // Create the service configuration
        Map<String, Object> config = new HashMap<>();
        config.put(LlmConfig.MODEL_PATH, modelPath);
        config.put(LlmConfig.THREADS, 4); // Use 4 threads for inference
        
        // Create the service
        service = new OrtLlmInferenceService(config);
        
        // Wait for the service to be ready
        int maxRetries = 10;
        int retryCount = 0;
        while (!service.isReady() && retryCount < maxRetries) {
            Thread.sleep(1000);
            retryCount++;
        }
        
        if (!service.isReady()) {
            throw new RuntimeException("Service failed to initialize within the timeout period");
        }
    }
    
    @AfterEach
    public void tearDown() {
        if (service != null) {
            service.close();
        }
    }
    
    /**
     * Test a chat interaction with the model.
     * This test demonstrates how to use OrtLlmInferenceService for a chat interaction.
     */
    @Test
//    @EnabledIfSystemProperty(named = "skipIntegrationTests", matches = "false|$")
    public void testChatInteraction() throws LlmInferenceException {
        // Create a list of messages for the chat
        List<Message> messages = new ArrayList<>();
        
        // Add a system message to set the behavior of the assistant
        messages.add(Message.system("You are a helpful assistant. Keep your answers brief and to the point."));
        
        // Add a user message
        messages.add(Message.user("What is the capital of France?"));
        
        // Create a chat request
        ChatRequest request = ChatRequest.builder()
                .addMessages(messages)
                .temperature(0.7)
                .maxTokens(100) // Limit to 100 tokens for faster response
                .build();
        
        // Get a response from the model
        ChatResponse response = service.chat(request);
        
        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getContent(), "Response content should not be null");
        assertFalse(response.getContent().isEmpty(), "Response content should not be empty");
        
        // Log the response
        logger.info("Model response: {}", response.getContent());
        logger.info("Response metadata: {}", response.getMetadata());
        
        // Verify that the response contains relevant information
        // Note: This is a simple check and may need to be adjusted based on the model's behavior
        String content = response.getContent().toLowerCase();
        assertTrue(content.contains("paris") || content.contains("france"), 
                "Response should contain relevant information about the capital of France");
    }
    
    /**
     * Test a multi-turn chat interaction with the model.
     * This test demonstrates how to use OrtLlmInferenceService for a multi-turn chat interaction.
     */
    @Test
//    @EnabledIfSystemProperty(named = "skipIntegrationTests", matches = "false|$")
    public void testMultiTurnChatInteraction() throws LlmInferenceException {
        // Create a list of messages for the chat
        List<Message> messages = new ArrayList<>();
        
        // Add a system message to set the behavior of the assistant
        messages.add(Message.system("You are a helpful assistant. Keep your answers brief and to the point."));
        
        // First turn
        messages.add(Message.user("Hello, how are you?"));
        
        // Create a chat request
        ChatRequest request = ChatRequest.builder()
                .addMessages(messages)
                .temperature(0.7)
                .maxTokens(50) // Limit to 50 tokens for faster response
                .build();
        
        // Get a response from the model
        ChatResponse response = service.chat(request);
        
        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getContent(), "Response content should not be null");
        assertFalse(response.getContent().isEmpty(), "Response content should not be empty");
        
        // Log the response
        logger.info("First turn response: {}", response.getContent());
        
        // Add the assistant's response to the conversation history
        messages.add(response.getResponse());
        
        // Second turn
        messages.add(Message.user("What can you help me with today?"));
        
        // Create a new chat request with the updated messages
        request = ChatRequest.builder()
                .addMessages(messages)
                .temperature(0.7)
                .maxTokens(100) // Limit to 100 tokens for faster response
                .build();
        
        // Get a response from the model
        response = service.chat(request);
        
        // Verify the response
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getContent(), "Response content should not be null");
        assertFalse(response.getContent().isEmpty(), "Response content should not be empty");
        
        // Log the response
        logger.info("Second turn response: {}", response.getContent());
        logger.info("Response metadata: {}", response.getMetadata());
    }
    
    /**
     * Downloads a file from a URL to a local path.
     *
     * @param fileUrl the URL of the file to download
     * @param destinationPath the local path to save the file to
     * @throws IOException if an I/O error occurs
     */
    private void downloadFile(String fileUrl, String destinationPath) throws IOException {
        logger.info("Downloading file from {} to {}", fileUrl, destinationPath);
        
        URL url = new URL(fileUrl);
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {
            
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        
        logger.info("File downloaded successfully");
    }
}
