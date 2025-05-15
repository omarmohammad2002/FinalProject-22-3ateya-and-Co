FROM openjdk:25-ea-4-jdk-oraclelinux9
WORKDIR /app
COPY target/*.jar userApp.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar","userApp.jar"]