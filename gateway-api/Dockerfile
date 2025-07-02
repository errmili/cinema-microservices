FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080

# Configuration Eureka - ACTIVÉ pour les tests
ENV EUREKA_CLIENT_ENABLED=true
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-server:8761/eureka

# Désactiver config server (pas nécessaire pour ce test)
ENV SPRING_CLOUD_CONFIG_ENABLED=false
ENV SPRING_CONFIG_IMPORT=""

# Configuration JWT (même clé que les autres services)
ENV APP_JWT_SECRET=LnX1yH3pYq6gTbWxV2zQ8uRfNjKrI0VxTk6b2o4D4q9w2U5QjX
ENV APP_JWT_EXPIRATIONMS=3600000

# Configuration Gateway
ENV SPRING_MAIN_WEB_APPLICATION_TYPE=reactive
ENV SPRING_CLOUD_GATEWAY_DISCOVERY_LOCATOR_ENABLED=true
ENV SPRING_CLOUD_GATEWAY_DISCOVERY_LOCATOR_LOWER_CASE_SERVICE_ID=true

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]