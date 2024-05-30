# First stage: Build the application
FROM maven:3.8.5-openjdk-17 AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code into the container
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Second stage: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the first stage
COPY --from=builder /app/target/*.jar app.jar


# Run the application
CMD ["java", "-jar", "app.jar"]
