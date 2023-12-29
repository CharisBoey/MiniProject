FROM maven:3-eclipse-temurin-21
WORKDIR /app

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN mvn package -Dmaven.test.skip=true

ENV PORT=8080 

ENV SPRING_REDIS_HOST=localhost 
ENV SPRING_REDIS_PORT=6379
ENV SPRING_REDIS_USERNAME=NOT_SET 
ENV SPRING_REDIS_PASSWORD=NOT_SET
ENV BOOKS_APIKEY=abc123

EXPOSE ${PORT}

ENTRYPOINT SERVER_PORT=${PORT} java -jar target/MiniProject-0.0.1-SNAPSHOT.jar
