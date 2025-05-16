FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app

# Create media directory and set permissions
RUN mkdir -p /app/media && chmod 777 /app/media

COPY target/*.jar /target/app.jar

ENTRYPOINT ["java", "-jar", "/target/app.jar"]