# JOSMI: Java ONNX Serving for Model Inference

JOSMI is a Java library for inference on LLM models in ONNX format. It provides a unified API for different inference approaches, making it easy to use ONNX models in Java applications.

## Features

- Unified API for LLM inference
- Support for chat and completion requests
- Multiple implementation options:
  - Direct ONNX Runtime (ORT) integration
  - Deep Java Library (DJL) with ONNX Runtime
  - REST client for Python FastAPI backend
  - REST client for DJL Serving backend
- Extensible architecture

## Modules

- **josmi-api**: Core API and interfaces
- **josmi-ort**: Implementation using ONNX Runtime
- **josmi-djl-ort**: Implementation using DJL with ONNX Runtime
- **josmi-rest-client-python-serving**: REST client for Python FastAPI backend
- **josmi-rest-client-djl-serving**: REST client for DJL Serving backend
- **josmi-examples**: Example usage of all modules
- **backends**: Backend implementations for serving ONNX models

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Building the Project

```bash
mvn clean install
```

### Running the Examples

#### Chat Example

```bash
cd josmi-examples
mvn exec:java -Dexec.mainClass="org.josmi.examples.ChatExample" -Dexec.args="ort /path/to/model.onnx"
```

#### Completion Example

```bash
cd josmi-examples
mvn exec:java -Dexec.mainClass="org.josmi.examples.CompletionExample" -Dexec.args="ort /path/to/model.onnx"
```

### Using the Library

#### 1. Add Dependencies

```xml
<dependencies>
    <!-- JOSMI API -->
    <dependency>
        <groupId>org.josmi</groupId>
        <artifactId>josmi-api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    
    <!-- Choose one or more implementations -->
    <dependency>
        <groupId>org.josmi</groupId>
        <artifactId>josmi-ort</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.josmi</groupId>
        <artifactId>josmi-djl-ort</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.josmi</groupId>
        <artifactId>josmi-rest-client-python-serving</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>org.josmi</groupId>
        <artifactId>josmi-rest-client-djl-serving</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

#### 2. Create a Service

```java
import org.josmi.api.LlmInferenceService;
import org.josmi.api.LlmInferenceServiceLoader;
import org.josmi.api.config.LlmConfig;

// Option 1: Using ONNX Runtime
LlmConfig ortConfig = LlmConfig.builder()
        .modelPath("/path/to/model.onnx")
        .build();
LlmInferenceService ortService = LlmInferenceServiceLoader.createService(
        "ort", ortConfig.getConfigMap());

// Option 2: Using DJL with ONNX Runtime
LlmConfig djlConfig = LlmConfig.builder()
        .modelPath("/path/to/model.onnx")
        .build();
LlmInferenceService djlService = LlmInferenceServiceLoader.createService(
        "djl-ort", djlConfig.getConfigMap());

// Option 3: Using Python FastAPI backend
LlmConfig pythonRestConfig = LlmConfig.builder()
        .endpointUrl("http://localhost:8000/v1")
        .timeoutMs(30000)
        .build();
LlmInferenceService pythonRestService = LlmInferenceServiceLoader.createService(
        "python-rest", pythonRestConfig.getConfigMap());

// Option 4: Using DJL Serving backend
LlmConfig djlServingRestConfig = LlmConfig.builder()
        .endpointUrl("http://localhost:8080/predictions/llm-onnx")
        .timeoutMs(30000)
        .build();
LlmInferenceService djlServingRestService = LlmInferenceServiceLoader.createService(
        "djl-serving-rest", djlServingRestConfig.getConfigMap());
```

#### 3. Chat with the Model

```java
import org.josmi.api.model.ChatRequest;
import org.josmi.api.model.ChatResponse;
import org.josmi.api.model.Message;

import java.util.ArrayList;
import java.util.List;

// Create a list of messages
List<Message> messages = new ArrayList<>();
messages.add(Message.system("You are a helpful assistant."));
messages.add(Message.user("Hello, how are you?"));

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
```

#### 4. Complete Text

```java
import org.josmi.api.model.CompletionRequest;
import org.josmi.api.model.CompletionResponse;

// Create a completion request
CompletionRequest request = CompletionRequest.builder()
        .prompt("Once upon a time")
        .temperature(0.7)
        .maxTokens(1024)
        .build();

// Get a response from the model
CompletionResponse response = service.complete(request);

// Print the response
System.out.println("Completion: " + response.getText());
```

## Backends

The project includes backend implementations for serving ONNX models:

- **Python FastAPI Backend**: A Python FastAPI backend for serving ONNX models.
- **DJL Serving Backend**: A DJL Serving backend for serving ONNX models.

See the [backends README](./backends/README.md) for more information.

## ONNX Models

You need to provide your own ONNX models for inference. You can convert models from various frameworks to ONNX format using tools like:

- [ONNX Converter](https://github.com/onnx/onnx)
- [PyTorch to ONNX](https://pytorch.org/docs/stable/onnx.html)
- [TensorFlow to ONNX](https://github.com/onnx/tensorflow-onnx)
- [Hugging Face Optimum](https://huggingface.co/docs/optimum/index)

## License

This project is licensed under the Apache License 2.0.
