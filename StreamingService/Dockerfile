FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app
EXPOSE 8080

# Create both media and static-media
RUN mkdir -p /app/media /app/static-media && chmod 777 /app/media

# Copy ad file to static-media (not overwritten)
COPY media/f47ac10b-01cc-4372-a567-0e02b2c3d479 /app/static-media/f47ac10b-01cc-4372-a567-0e02b2c3d479

COPY target/*.jar /target/app.jar

ENTRYPOINT ["java", "-jar", "/target/app.jar"]