# DJL Serving Backend for ONNX Model Inference

This is a DJL Serving backend for serving ONNX models for LLM inference. It's designed to be used with the JOSMI REST client.

## Features

- REST API for model inference
- Support for ONNX models
- Health check endpoint
- Docker support

## Requirements

- Java 11 or higher
- DJL Serving 0.25.0 or higher

## Installation

### Option 1: Using Docker

1. Build the Docker image:

```bash
docker build -t djl-serving-onnx .
```

2. Run the Docker container:

```bash
docker run -p 8080:8080 -p 8081:8081 -v /path/to/models:/opt/djl/models/llm-onnx djl-serving-onnx
```

### Option 2: Using DJL Serving directly

1. Download DJL Serving from https://publish.djl.ai/djl-serving/serving-0.25.0.zip
2. Extract the zip file
3. Copy the configuration files to the DJL Serving directory
4. Run DJL Serving:

```bash
./serving -f serving.properties
```

## Configuration

### serving.properties

This file contains the server configuration:

```properties
inference_address=http://0.0.0.0:8080
management_address=http://0.0.0.0:8081
model_store=models
number_of_netty_threads=4
job_queue_size=1000
max_idle_time=60
max_worker_threads=0
```

### model.json

This file contains the model configuration:

```json
{
  "modelName": "llm-onnx",
  "modelVersion": "1.0",
  "engine": "OnnxRuntime",
  "application": "llm",
  "options": {
    "mapInputNames": "input_ids,attention_mask",
    "mapOutputNames": "logits",
    "optLevel": 99,
    "interOpNumThreads": 1,
    "intraOpNumThreads": 4
  },
  "translatorFactory": "ai.djl.onnxruntime.zoo.OrtTranslatorFactory",
  "inputs": [
    {
      "name": "data",
      "description": "Input text for the model"
    }
  ],
  "outputs": [
    {
      "name": "data",
      "description": "Generated text from the model"
    }
  ],
  "parameters": [
    {
      "name": "temperature",
      "type": "float",
      "description": "Controls randomness in the output.",
      "defaultValue": 0.7
    },
    {
      "name": "max_tokens",
      "type": "int",
      "description": "The maximum number of tokens to generate.",
      "defaultValue": 1024
    },
    {
      "name": "top_p",
      "type": "float",
      "description": "Controls diversity via nucleus sampling.",
      "defaultValue": 1.0
    },
    {
      "name": "top_k",
      "type": "int",
      "description": "Controls diversity by limiting to the top k most likely next tokens.",
      "defaultValue": 50
    },
    {
      "name": "repetition_penalty",
      "type": "float",
      "description": "Penalizes repetition in the output.",
      "defaultValue": 1.0
    }
  ]
}
```

## API Endpoints

### Inference

```
POST /predictions/llm-onnx
```

Request body:

```json
{
  "data": "Once upon a time",
  "parameters": {
    "temperature": 0.7,
    "max_tokens": 1024,
    "top_p": 1.0,
    "top_k": 50,
    "repetition_penalty": 1.0
  }
}
```

Response:

```json
{
  "data": "Once upon a time, there was a kingdom far, far away...",
  "metrics": {
    "latency": 1234,
    "tokens": 15
  }
}
```

### Health Check

```
GET /ping
```

Response:

```json
{
  "status": "Healthy"
}
```

## Using with JOSMI

This backend is designed to be used with the JOSMI REST client. To use it, configure the JOSMI REST client with the endpoint URL of this server.

Example:

```java
LlmConfig config = LlmConfig.builder()
    .endpointUrl("http://localhost:8080/predictions/llm-onnx")
    .timeoutMs(30000)
    .build();

LlmInferenceService service = LlmInferenceServiceLoader.createService(
    "djl-serving-rest", config.getConfigMap());
```

## License

This project is licensed under the Apache License 2.0.
