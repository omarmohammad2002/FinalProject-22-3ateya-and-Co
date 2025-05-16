FROM openjdk:25-ea-4-jdk-oraclelinux9

WORKDIR /app

COPY target/*.jar target/MusicService.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "target/MusicService.jar"]