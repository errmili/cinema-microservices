FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8761

ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8761
ENV EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
ENV EUREKA_CLIENT_FETCH_REGISTRY=false
ENV EUREKA_SERVER_ENABLE_SELF_PRESERVATION=false

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

## ============================
## Étape de construction
## ============================
#FROM maven:3.8.7-openjdk-18 AS build
#
## Définir le répertoire de travail
#WORKDIR /build
#
## Copier le fichier pom.xml et télécharger les dépendances
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
## Copier le code source et construire l'application
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## ============================
## Étape d'exécution (Runtime)
## ============================
#FROM amazoncorretto:17
#
## Définir le répertoire de travail pour l'exécution de l'application
#WORKDIR /app
#
## Copier le fichier JAR généré par l'étape de build dans le conteneur
#COPY --from=build /build/target/discovery-server-0.0.1-SNAPSHOT.jar /app/eureka-server.jar
#
## Exposer le port sur lequel Eureka sera accessible (par défaut 8761)
#EXPOSE 8761
#
## Définir les variables d'environnement pour Eureka
#ENV EUREKA_SERVER_PORT=8761
#ENV EUREKA_SERVER_HOST=localhost
#
## Commande de démarrage de l'application Eureka
#CMD ["java", "-jar", "/app/eureka-server.jar"]