FROM eclipse-temurin:26-jdk
WORKDIR /app
COPY ./target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]