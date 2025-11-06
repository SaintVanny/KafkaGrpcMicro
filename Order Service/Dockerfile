FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/Orders-0.0.1-SNAPSHOT.jar OrderServiceApp.jar
ENTRYPOINT ["java", "-jar", "OrderServiceApp.jar"]
