# Python FastAPI Backend for ONNX Model Inference

This is a simple FastAPI backend for serving ONNX models for LLM inference. It's designed to be used with the JOSMI REST client.

## Features

- REST API for chat and completion requests
- Support for ONNX models
- Health check endpoint
- Docker support

## Requirements

- Python 3.8+
- FastAPI
- Uvicorn
- ONNX Runtime
- NumPy

## Installation

1. Clone the repository
2. Install dependencies:

```bash
pip install -r requirements.txt
```

## Usage

### Running the server

```bash
python app.py --model /path/to/model.onnx --host 0.0.0.0 --port 8000
```

If no model is specified, the server will use dummy responses for testing.

### Using Docker

Build the Docker image:

```bash
docker build -t onnx-llm-server .
```

Run the Docker container:

```bash
docker run -p 8000:8000 -v /path/to/models:/models onnx-llm-server python app.py --model /models/model.onnx
```

## API Endpoints

### Chat

```
POST /v1/chat
```

Request body:

```json
{
  "messages": [
    {
      "role": "system",
      "content": "You are a helpful assistant."
    },
    {
      "role": "user",
      "content": "Hello, how are you?"
    }
  ],
  "parameters": {
    "temperature": 0.7,
    "max_tokens": 1024
  }
}
```

Response:

```json
{
  "response": {
    "role": "assistant",
    "content": "I'm doing well, thank you for asking! How can I assist you today?"
  },
  "metadata": {
    "prompt_tokens": 42,
    "completion_tokens": 15,
    "total_tokens": 57,
    "model": "/path/to/model.onnx",
    "temperature": 0.7,
    "max_tokens": 1024
  }
}
```

### Completion

```
POST /v1/completion
```

Request body:

```json
{
  "prompt": "Once upon a time",
  "parameters": {
    "temperature": 0.7,
    "max_tokens": 1024
  }
}
```

Response:

```json
{
  "text": "Once upon a time, there was a kingdom far, far away...",
  "metadata": {
    "prompt_tokens": 4,
    "completion_tokens": 11,
    "total_tokens": 15,
    "model": "/path/to/model.onnx",
    "temperature": 0.7,
    "max_tokens": 1024
  }
}
```

### Health Check

```
POST /v1/health
```

Response:

```json
{
  "status": "ok"
}
```

## Using with JOSMI

This backend is designed to be used with the JOSMI REST client. To use it, configure the JOSMI REST client with the endpoint URL of this server.

Example:

```java
LlmConfig config = LlmConfig.builder()
    .endpointUrl("http://localhost:8000/v1")
    .timeoutMs(30000)
    .build();

LlmInferenceService service = LlmInferenceServiceLoader.createService(
    "python-rest", config.getConfigMap());
```

## License

This project is licensed under the Apache License 2.0.
