FROM openjdk:25-ea-4-jdk-oraclelinux9

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/SocialMediaService-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]