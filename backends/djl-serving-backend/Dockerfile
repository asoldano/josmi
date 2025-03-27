FROM amazoncorretto:11-alpine

# Install required packages
RUN apk add --no-cache bash curl

# Set environment variables
ENV DJL_SERVING_VERSION=0.25.0
ENV MODEL_STORE=/opt/djl/models

# Create directories
RUN mkdir -p /opt/djl/conf /opt/djl/models

# Download DJL Serving
RUN curl -L https://publish.djl.ai/djl-serving/serving-${DJL_SERVING_VERSION}.zip -o /tmp/djl-serving.zip && \
    unzip /tmp/djl-serving.zip -d /opt/djl && \
    rm /tmp/djl-serving.zip && \
    ln -s /opt/djl/serving-${DJL_SERVING_VERSION} /opt/djl/serving

# Copy configuration files
COPY serving.properties /opt/djl/conf/
COPY models/ /opt/djl/models/

# Expose ports
EXPOSE 8080 8081

# Set working directory
WORKDIR /opt/djl

# Run DJL Serving
CMD ["/opt/djl/serving/bin/serving", "-f", "/opt/djl/conf/serving.properties"]
