FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY ./target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]