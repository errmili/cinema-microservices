FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8888

# Variables d'environnement
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8888

# Configuration Eureka - ACTIVÉ pour les tests
ENV EUREKA_CLIENT_ENABLED=true
ENV EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://discovery-server:8761/eureka

# Configuration Git pour le config server
ENV SPRING_CLOUD_CONFIG_SERVER_GIT_URI=https://github.com/errmili/config-repo.git
ENV SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULT_LABEL=main
ENV SPRING_CLOUD_CONFIG_SERVER_GIT_CLONE_ON_START=true

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

## Utilisation d'une image officielle de Java comme base
#FROM openjdk:17-jdk-slim
#
## Définir le répertoire de travail
#WORKDIR /app
#
## Copier le fichier jar du projet dans le conteneur
#COPY target/demo-0.0.1-SNAPSHOT.jar /app/config-server.jar
#
## Exposer le port 8888, où le service sera accessible
#EXPOSE 8888
#
## Commande pour démarrer l'application Spring Boot
#ENTRYPOINT ["java", "-jar", "config-server.jar"]
