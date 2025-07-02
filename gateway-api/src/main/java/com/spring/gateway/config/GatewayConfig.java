package com.spring.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// Indique que cette classe est une configuration Spring (pour définir des beans et configurer des composants)
@Configuration
public class GatewayConfig {

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

    // ✅ AJOUTEZ CE BEAN POUR CORS
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // ✅ Autoriser Angular (localhost:4200)
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedOrigin("http://localhost:3000"); // Au cas où

        // ✅ Méthodes HTTP autorisées
        corsConfig.addAllowedMethod("GET");
        corsConfig.addAllowedMethod("POST");
        corsConfig.addAllowedMethod("PUT");
        corsConfig.addAllowedMethod("DELETE");
        corsConfig.addAllowedMethod("OPTIONS");

        // ✅ Headers autorisés
        corsConfig.addAllowedHeader("*");

        // ✅ Autoriser les credentials (important pour JWT)
        corsConfig.setAllowCredentials(true);

        // ✅ Cache preflight
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}

/*
Un API Gateway est un point d’entrée unique pour tous les appels vers tes microservices. Il reçoit les requêtes des clients,
puis les redirige vers le microservice approprié selon les règles que tu définis (comme ici, la route vers un service de gestion
des utilisateurs).
 */