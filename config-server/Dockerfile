FROM eclipse-temurin:26-jdk

WORKDIR /app

COPY ./target/*.jar app.jar

RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
