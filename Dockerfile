# Stage 1: Build the JAR with Maven
# Use an image with Maven and Java 21; name this stage "build" for later reference.
FROM maven:3.9-eclipse-temurin-21 AS build
# Set /app as the working directory inside the build image.
WORKDIR /app
# Copy the build context (excluding files in .dockerignore) into /app.
COPY . .
# Remove previous build output and package the application JAR without running tests.
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR in a slim runtime image
# Start a separate, smaller Alpine-based image with only the Java 21 runtime.
FROM eclipse-temurin:21-jre-alpine
# Set /app as the working directory inside the runtime image.
WORKDIR /app
# Copy the built JAR from the build stage into the runtime image as app.jar.
COPY --from=build /app/target/*.jar app.jar
# Document the application's port; use docker run -p to publish it on the host.
EXPOSE 8080
# Set the default command to launch the application when the container starts.
CMD ["java", "-jar", "app.jar"]