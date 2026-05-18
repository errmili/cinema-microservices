package com.spring.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// 💡 @Configuration sert à dire à Spring : "Cette classe contient des configurations importantes"
//    Spring va la lire au démarrage et appliquer tout ce qu'elle contient automatiquement
@Configuration
public class GatewayConfig {

    // ✅ AJOUTEZ CE BEAN POUR CORS
    // 💡 @Bean sert à créer un objet géré par Spring
    //    Ici on crée le filtre CORS qui sera actif sur TOUTES les requêtes entrantes
    //    CORS = Cross-Origin Resource Sharing : c'est la règle qui dit
    //    "qui a le droit d'appeler mon API depuis un navigateur ?"
    @Bean
    public CorsWebFilter corsWebFilter() {

        // 💡 CorsConfiguration sert à définir les règles CORS
        //    C'est comme rédiger la liste des invités autorisés à entrer
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ✅ Autoriser Angular (localhost:4200)
        // 💡 Cela sert à autoriser uniquement ces origines (adresses) à appeler notre API
        //    Si une autre adresse essaie d'appeler l'API depuis un navigateur → le navigateur bloque !
        //    Exemple : ton frontend Angular tourne sur localhost:4200,
        //    sans cette ligne, le navigateur refuserait la connexion
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedOrigin("http://localhost:3000"); // Au cas où

        // ✅ Méthodes HTTP autorisées
        // 💡 Cela sert à définir quelles actions HTTP sont permises depuis le navigateur
        //    GET    → lire des données
        //    POST   → créer des données
        //    PUT    → modifier des données
        //    DELETE → supprimer des données
        //    OPTIONS → requête préliminaire envoyée automatiquement par le navigateur (preflight)
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");

        // ✅ Headers autorisés
        // 💡 Le "*" sert à autoriser TOUS les headers HTTP
        //    C'est important car notre frontend envoie des headers comme :
        //    "Authorization: Bearer eyJ..." pour le JWT
        //    "Content-Type: application/json" pour le format des données
        corsConfig.addAllowedHeader("*");

        // ✅ Autoriser les credentials (important pour JWT)
        // 💡 Cela sert à autoriser l'envoi de cookies et du header Authorization
        //    Sans cette ligne, le navigateur n'enverrait pas le token JWT automatiquement
        //    ⚠️ Important : quand allowCredentials = true,
        //    on NE PEUT PAS utiliser "*" pour les origines → il faut les lister explicitement (ce qu'on fait au-dessus)
        corsConfig.setAllowCredentials(true);

        // ✅ Cache preflight
        // 💡 Cela sert à mettre en cache la réponse de la requête OPTIONS (preflight) pendant 3600 secondes (1 heure)
        //    Avant chaque vraie requête (POST, PUT...), le navigateur envoie d'abord une requête OPTIONS pour demander
        //    "Est-ce que j'ai le droit d'appeler cette API ?"
        //    Sans ce cache, cette vérification serait refaite à CHAQUE fois → perte de performance
        corsConfig.setMaxAge(3600L);

        // 💡 UrlBasedCorsConfigurationSource sert à associer les règles CORS à des chemins d'URL
        //    C'est ici qu'on dit : "applique ces règles sur TOUTES les routes"
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 💡 Le "/**" sert à cibler TOUTES les routes de la gateway
        //    Exemple : /api/users/**, /api/orders/**, /api/auth/** → toutes sont concernées
        source.registerCorsConfiguration("/**", corsConfig);

        // 💡 On retourne le filtre final avec toutes les règles configurées
        //    Spring va automatiquement l'appliquer sur chaque requête qui entre dans la gateway
        return new CorsWebFilter(source);
    }
}


/*
Un API Gateway est un point d’entrée unique pour tous les appels vers tes microservices. Il reçoit les requêtes des clients,
puis les redirige vers le microservice approprié selon les règles que tu définis (comme ici, la route vers un service de gestion
des utilisateurs).
 */
















    /*
    // Crée un bean (un composant Spring) qui définit les règles de routage (les "routes") dans le gateway
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        // On utilise le builder pour construire la liste des routes
        return builder.routes()
                .route("user-management", r -> r
                        // Cette route s'applique à toutes les requêtes HTTP dont le chemin commence par "/api/auth/"
                        .path("/api/auth/**")
                        // Toutes ces requêtes seront redirigées vers ce service qui tourne sur localhost port 8083
                        .uri("http://localhost:8083"))

                        // Route pour le service Cinema (port 9090)
                        .route("cinema-service", r -> r
                                .path("/cinema/v1/**")
                                .uri("http://localhost:9090"))

                        // Route pour le service Movies (adaptez le port selon votre config)
                        .route("movies-service", r -> r
                                .path("/movies/v1/**")
                                .uri("http://localhost:7080"))  // ✅ Adaptez le port si nécessaire

                        // Route pour le service Reservations/Tickets (port 7000)
                        .route("reservation-service", r -> r
                                .path("/reservations/v1/**")
                                .uri("http://localhost:7000"))
                .build();
    }*/