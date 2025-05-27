FROM eclipse-temurin:17-jdk-jammy

ARG JAR_FILE=target/micro-product-0.0.1-SNAPSHOT.jar

WORKDIR /app
EXPOSE 8086
COPY ${JAR_FILE} product-app.jar
ENTRYPOINT ["java","-jar","product-app.jar"]