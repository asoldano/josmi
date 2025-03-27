# JOSMI Backends

This directory contains backend implementations for serving ONNX models for LLM inference. These backends are designed to be used with the JOSMI REST clients.

## Available Backends

- [Python FastAPI Backend](./python-fastapi-backend/README.md): A Python FastAPI backend for serving ONNX models.
- [DJL Serving Backend](./djl-serving-backend/README.md): A DJL Serving backend for serving ONNX models.

## Running the Backends

### Using Docker Compose

The easiest way to run both backends is to use Docker Compose:

```bash
docker-compose up
```

This will start both backends:
- Python FastAPI Backend: http://localhost:8000
- DJL Serving Backend: http://localhost:8080

### Using Docker

You can also run each backend separately using Docker:

#### Python FastAPI Backend

```bash
cd python-fastapi-backend
docker build -t python-fastapi-backend .
docker run -p 8000:8000 -v /path/to/models:/models python-fastapi-backend python app.py --model /models/model.onnx
```

#### DJL Serving Backend

```bash
cd djl-serving-backend
docker build -t djl-serving-backend .
docker run -p 8080:8080 -p 8081:8081 -v /path/to/models:/opt/djl/models/llm-onnx djl-serving-backend
```

### Running Locally

You can also run each backend locally:

#### Python FastAPI Backend

```bash
cd python-fastapi-backend
pip install -r requirements.txt
python app.py --model /path/to/model.onnx
```

#### DJL Serving Backend

```bash
cd djl-serving-backend
# Download DJL Serving
curl -L https://publish.djl.ai/djl-serving/serving-0.25.0.zip -o djl-serving.zip
unzip djl-serving.zip
# Run DJL Serving
./serving-0.25.0/bin/serving -f serving.properties
```

## Using with JOSMI

These backends are designed to be used with the JOSMI REST clients. To use them, configure the JOSMI REST client with the endpoint URL of the backend.

### Python FastAPI Backend

```java
LlmConfig config = LlmConfig.builder()
    .endpointUrl("http://localhost:8000/v1")
    .timeoutMs(30000)
    .build();

LlmInferenceService service = LlmInferenceServiceLoader.createService(
    "python-rest", config.getConfigMap());
```

### DJL Serving Backend

```java
LlmConfig config = LlmConfig.builder()
    .endpointUrl("http://localhost:8080/predictions/llm-onnx")
    .timeoutMs(30000)
    .build();

LlmInferenceService service = LlmInferenceServiceLoader.createService(
    "djl-serving-rest", config.getConfigMap());
```

## ONNX Models

You need to provide your own ONNX models for inference. You can convert models from various frameworks to ONNX format using tools like:

- [ONNX Converter](https://github.com/onnx/onnx)
- [PyTorch to ONNX](https://pytorch.org/docs/stable/onnx.html)
- [TensorFlow to ONNX](https://github.com/onnx/tensorflow-onnx)
- [Hugging Face Optimum](https://huggingface.co/docs/optimum/index)

Place your ONNX models in the `models` directory to use them with the backends.

## License

This project is licensed under the Apache License 2.0.
