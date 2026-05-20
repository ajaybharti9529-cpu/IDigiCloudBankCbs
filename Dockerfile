# Use lightweight Java image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/cbs-mock-service-1.0.0.jar app.jar

# Expose Render port
EXPOSE 7717

# Start application
ENTRYPOINT ["java","-jar","app.jar"]