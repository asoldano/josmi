#!/usr/bin/env python
"""
FastAPI backend for ONNX model inference.
This backend is used for testing the JOSMI REST client.
"""

import argparse
import logging
import os
import time
from typing import Dict, List, Optional, Any

import onnxruntime as ort
import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import uvicorn

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Create FastAPI app
app = FastAPI(title="ONNX LLM Inference API")

# Global variables
model_path = None
session = None
tokenizer = None

class Message(BaseModel):
    """Message model for chat requests and responses."""
    role: str
    content: str

class ChatRequest(BaseModel):
    """Chat request model."""
    messages: List[Message]
    parameters: Dict[str, Any] = Field(default_factory=dict)

class ChatResponse(BaseModel):
    """Chat response model."""
    response: Message
    metadata: Dict[str, Any] = Field(default_factory=dict)

class CompletionRequest(BaseModel):
    """Completion request model."""
    prompt: str
    parameters: Dict[str, Any] = Field(default_factory=dict)

class CompletionResponse(BaseModel):
    """Completion response model."""
    text: str
    metadata: Dict[str, Any] = Field(default_factory=dict)

def load_model(model_path: str):
    """Load the ONNX model."""
    global session
    
    logger.info(f"Loading model from {model_path}")
    
    # Create session options
    session_options = ort.SessionOptions()
    
    # Set graph optimization level
    session_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
    
    # Create session
    session = ort.InferenceSession(model_path, session_options)
    
    logger.info("Model loaded successfully")
    
    # Log model inputs and outputs
    logger.info("Model inputs:")
    for input_info in session.get_inputs():
        logger.info(f"  {input_info.name}: {input_info.type} {input_info.shape}")
    
    logger.info("Model outputs:")
    for output_info in session.get_outputs():
        logger.info(f"  {output_info.name}: {output_info.type} {output_info.shape}")

def format_chat_messages(messages: List[Message]) -> str:
    """Format chat messages into a prompt string."""
    prompt = ""
    
    for message in messages:
        role = message.role.lower()
        content = message.content
        
        if role == "system":
            prompt += f"<|system|>\n{content}\n"
        elif role == "user":
            prompt += f"<|user|>\n{content}\n"
        elif role == "assistant":
            prompt += f"<|assistant|>\n{content}\n"
        else:
            prompt += f"{content}\n"
    
    # Add assistant prefix for the response
    prompt += "<|assistant|>\n"
    
    return prompt

def generate_dummy_response(prompt: str, parameters: Dict[str, Any]) -> Dict[str, Any]:
    """
    Generate a dummy response for testing purposes.
    In a real implementation, this would use the ONNX model for inference.
    """
    # Get parameters with defaults
    temperature = parameters.get("temperature", 0.7)
    max_tokens = parameters.get("max_tokens", 1024)
    
    # Simulate processing time
    time.sleep(0.5)
    
    # Generate a dummy response
    if "chat" in prompt.lower():
        response_text = "I'm a dummy LLM response for chat. I'm not using a real model for inference."
    else:
        response_text = "I'm a dummy LLM response for completion. I'm not using a real model for inference."
    
    # Add some details from the prompt
    if len(prompt) > 20:
        response_text += f" Your prompt was: '{prompt[:20]}...'"
    else:
        response_text += f" Your prompt was: '{prompt}'"
    
    # Add some details about the parameters
    response_text += f" (temperature={temperature}, max_tokens={max_tokens})"
    
    # Create metadata
    metadata = {
        "prompt_tokens": len(prompt),
        "completion_tokens": len(response_text),
        "total_tokens": len(prompt) + len(response_text),
        "model": model_path or "dummy-model",
        "temperature": temperature,
        "max_tokens": max_tokens,
    }
    
    return {
        "text": response_text,
        "metadata": metadata,
    }

@app.post("/v1/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """Chat endpoint."""
    try:
        # Format chat messages into a prompt
        prompt = format_chat_messages(request.messages)
        
        # Generate response
        result = generate_dummy_response(prompt, request.parameters)
        
        # Create response
        return ChatResponse(
            response=Message(role="assistant", content=result["text"]),
            metadata=result["metadata"],
        )
    except Exception as e:
        logger.error(f"Error in chat endpoint: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/v1/completion", response_model=CompletionResponse)
async def completion(request: CompletionRequest):
    """Completion endpoint."""
    try:
        # Generate response
        result = generate_dummy_response(request.prompt, request.parameters)
        
        # Create response
        return CompletionResponse(
            text=result["text"],
            metadata=result["metadata"],
        )
    except Exception as e:
        logger.error(f"Error in completion endpoint: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/v1/health")
async def health():
    """Health check endpoint."""
    return {"status": "ok"}

def main():
    """Main function."""
    parser = argparse.ArgumentParser(description="FastAPI backend for ONNX model inference")
    parser.add_argument("--model", type=str, help="Path to the ONNX model file")
    parser.add_argument("--host", type=str, default="0.0.0.0", help="Host to bind to")
    parser.add_argument("--port", type=int, default=8000, help="Port to bind to")
    
    args = parser.parse_args()
    
    global model_path
    model_path = args.model
    
    # Load model if specified
    if model_path:
        load_model(model_path)
    else:
        logger.warning("No model specified, using dummy responses")
    
    # Start server
    uvicorn.run(app, host=args.host, port=args.port)

if __name__ == "__main__":
    main()
