FROM golang:1.20 as builder
# Set the Current Working Directory inside the container
WORKDIR /app
# Copy the source from the current directory to the Working Directory inside the container
COPY . .
# Build the Go app
RUN go mod init hello-world && go mod tidy && CGO_ENABLED=0 go build -o hello-world .