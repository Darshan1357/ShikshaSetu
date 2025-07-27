FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . /app
RUN ./mvnw clean package -DskipTests
EXPOSE 8081
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
