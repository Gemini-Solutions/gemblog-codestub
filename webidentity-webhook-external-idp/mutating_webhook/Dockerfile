# Use an official Python runtime as a parent image
FROM python:3.9-slim

# Set the working directory in the container
WORKDIR /app

COPY requirements.txt requirements.txt

# Install any needed dependencies specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

COPY server.crt server.crt
COPY server.key server.key
COPY mutating_webhook.py mutating_webhook.py

ENTRYPOINT ["python", "mutating_webhook.py"]