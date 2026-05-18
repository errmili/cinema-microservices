package com.spring.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🛡️ FALLBACK CONTROLLER - RÉPONSES DE SECOURS
 *
 * POURQUOI CE CONTROLLER ?
 * ========================
 * Quand un microservice tombe (500, timeout, etc.), au lieu de renvoyer une erreur brute,
 * on renvoie une réponse "de secours" (fallback) élégante qui informe l'utilisateur.
 *
 * EXEMPLE CONCRET :
 * =================
 * SANS FALLBACK :
 * User → Gateway → Movie-Service (DOWN)
 *        ↓
 *     ERROR 500: "Connection timeout"
 *
 * ❌ L'utilisateur ne comprend pas ce qui se passe
 *
 *
 * AVEC FALLBACK :
 * User → Gateway → Movie-Service (DOWN)
 *        ↓
 *     Circuit Breaker OPEN
 *        ↓
 *     Fallback Response:
 *     {
 *       "status": 503,
 *       "message": "Le service de films est temporairement indisponible",
 *       "fallback": true,
 *       "timestamp": "2025-10-29T17:30:00"
 *     }
 *
 * ✅ L'utilisateur comprend le problème
 * ✅ Réponse professionnelle
 * ✅ L'application ne crash pas
 *
 *
 * ROUTES FALLBACK PAR SERVICE :
 * ============================
 * /fallback/movie-service    → Fallback pour movie-service
 * /fallback/cinema-service   → Fallback pour cinema-service
 * /fallback/booking-service  → Fallback pour booking-service
 * /fallback/user-management  → Fallback pour user-management
 * /fallback/default          → Fallback générique
 */

// 💡 @RestController sert à dire à Spring : "Cette classe est un contrôleur REST"
//    Elle peut recevoir des requêtes HTTP et retourner des réponses JSON automatiquement
// 💡 @RequestMapping("/fallback") sert à définir le préfixe de toutes les routes de cette classe
//    Toutes les méthodes ici seront accessibles sous /fallback/...
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    // 💡 Cela sert à écrire des messages dans les logs quand un fallback est activé
    //    On saura exactement quel service est tombé et à quelle heure
    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    /**
     * 🎬 FALLBACK MOVIE SERVICE
     * Appelé quand movie-service est DOWN
     */
    // 💡 Ces 4 annotations servent à intercepter TOUS les types de requêtes HTTP
    //    GET, POST, PUT, DELETE → peu importe ce que le client envoyait,
    //    si movie-service est mort, toutes ses requêtes atterrissent ici
    @GetMapping("/movie-service")
    @PostMapping("/movie-service")
    @PutMapping("/movie-service")
    @DeleteMapping("/movie-service")
    // 💡 Mono<ResponseEntity<...>> : c'est la façon "réactive" de retourner une réponse en Spring WebFlux
    //    Mono = un seul résultat asynchrone (comme une Promise en JavaScript)
    //    ResponseEntity = une réponse HTTP complète avec son statut (503, 200...) + son body
    //    Map<String, Object> = le body de la réponse en JSON
    public Mono<ResponseEntity<Map<String, Object>>> movieServiceFallback() {
        logger.warn("🎬 ⚠️ FALLBACK activé pour MOVIE-SERVICE");

        // 💡 Mono.just(...) sert à emballer une valeur dans un Mono
        //    C'est comme dire "retourne immédiatement cette réponse de façon réactive"
        return Mono.just(ResponseEntity
                // 💡 503 SERVICE_UNAVAILABLE = "Le serveur existe mais le service est temporairement indisponible"
                //    C'est plus précis que 500 (erreur interne) : ici on sait que c'est un service en panne
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse(
                        "Le service de films est temporairement indisponible. Réessayez dans quelques instants.",
                        "movie-service"
                )));
    }

    /**
     * 🎭 FALLBACK CINEMA SERVICE
     * Appelé quand cinema-service est DOWN
     */
    @GetMapping("/cinema-service")
    @PostMapping("/cinema-service")
    @PutMapping("/cinema-service")
    @DeleteMapping("/cinema-service")
    public Mono<ResponseEntity<Map<String, Object>>> cinemaServiceFallback() {
        logger.warn("🎭 ⚠️ FALLBACK activé pour CINEMA-SERVICE");

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse(
                        "Le service de cinémas est temporairement indisponible. Réessayez dans quelques instants.",
                        "cinema-service"
                )));
    }

    /**
     * 🎫 FALLBACK BOOKING SERVICE
     * Appelé quand booking-service est DOWN
     */
    @GetMapping("/booking-service")
    @PostMapping("/booking-service")
    @PutMapping("/booking-service")
    @DeleteMapping("/booking-service")
    public Mono<ResponseEntity<Map<String, Object>>> bookingServiceFallback() {
        logger.warn("🎫 ⚠️ FALLBACK activé pour BOOKING-SERVICE");

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse(
                        "Le service de réservation est temporairement indisponible. Réessayez dans quelques instants.",
                        "booking-service"
                )));
    }

    /**
     * 👤 FALLBACK USER MANAGEMENT
     * Appelé quand user-management est DOWN
     */
    @GetMapping("/user-management")
    @PostMapping("/user-management")
    @PutMapping("/user-management")
    @DeleteMapping("/user-management")
    public Mono<ResponseEntity<Map<String, Object>>> userManagementFallback() {
        logger.warn("👤 ⚠️ FALLBACK activé pour USER-MANAGEMENT");

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse(
                        "Le service d'authentification est temporairement indisponible. Réessayez dans quelques instants.",
                        "user-management"
                )));
    }

    /**
     * 🌐 FALLBACK DEFAULT (générique)
     * Appelé pour tout autre service
     */
    // 💡 Ce fallback "default" sert de filet de sécurité pour n'importe quel service
    //    non listé au-dessus → on ne laisse jamais le client sans réponse
    @GetMapping("/default")
    @PostMapping("/default")
    @PutMapping("/default")
    @DeleteMapping("/default")
    public Mono<ResponseEntity<Map<String, Object>>> defaultFallback() {
        logger.warn("🌐 ⚠️ FALLBACK DEFAULT activé");

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createFallbackResponse(
                        "Le service demandé est temporairement indisponible. Réessayez dans quelques instants.",
                        "unknown-service"
                )));
    }

    /**
     * 🛠️ MÉTHODE UTILITAIRE : Créer une réponse fallback standardisée
     *
     * @param message Message d'erreur user-friendly
     * @param serviceName Nom du service en panne
     * @return Map contenant la réponse fallback
     */
    // 💡 Cette méthode privée sert à centraliser la construction de la réponse JSON
    //    Au lieu de répéter le même code dans chaque fallback, on l'écrit UNE SEULE FOIS ici
    //    C'est le principe DRY : "Don't Repeat Yourself" → règle d'or du développeur Senior !
    private Map<String, Object> createFallbackResponse(String message, String serviceName) {

        // 💡 HashMap sert à construire un objet JSON clé/valeur
        //    Chaque response.put("clé", valeur) = une propriété dans le JSON retourné au client
        Map<String, Object> response = new HashMap<>();

        // Informations principales
        // 💡 On retourne le code HTTP 503 dans le body aussi (pas seulement dans le header)
        //    pour que le frontend puisse l'afficher facilement
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value()); // → 503
        response.put("error", "Service Unavailable");
        response.put("message", message); // → message lisible par l'utilisateur

        // Métadonnées utiles
        // 💡 "fallback: true" sert à indiquer au frontend que c'est une réponse de secours
        //    Le frontend peut ainsi afficher un message spécial ou une UI dégradée
        response.put("fallback", true);  // Indique que c'est une réponse fallback
        // 💡 "service" sert à savoir quel microservice est en panne dans les logs ou le monitoring
        response.put("service", serviceName);
        // 💡 "timestamp" sert à horodater la panne → utile pour les rapports d'incidents
        response.put("timestamp", LocalDateTime.now().toString());

        // 💡 BONUS : Suggestions pour l'utilisateur
        response.put("suggestion", "Veuillez patienter quelques instants et réessayer");

        // 💡 BONUS : Support (en production, mettre vraie URL)
        response.put("support", "Si le problème persiste, contactez le support");

        return response;
        // 💡 Ce que le client reçoit finalement ressemble à ça en JSON :
        // {
        //   "status": 503,
        //   "error": "Service Unavailable",
        //   "message": "Le service de films est temporairement indisponible...",
        //   "fallback": true,
        //   "service": "movie-service",
        //   "timestamp": "2024-01-15T10:30:01",
        //   "suggestion": "Veuillez patienter...",
        //   "support": "Si le problème persiste..."
        // }
    }
}

/**
 * 💡 EXEMPLE DE RÉPONSE FALLBACK
 * ===============================
 *
 * Quand movie-service est DOWN, l'utilisateur reçoit :
 *
 * HTTP 503 Service Unavailable
 * {
 *   "status": 503,
 *   "error": "Service Unavailable",
 *   "message": "Le service de films est temporairement indisponible. Réessayez dans quelques instants.",
 *   "fallback": true,
 *   "service": "movie-service",
 *   "timestamp": "2025-10-29T17:30:00.123",
 *   "suggestion": "Veuillez patienter quelques instants et réessayer",
 *   "support": "Si le problème persiste, contactez le support"
 * }
 *
 * C'est beaucoup mieux que :
 * HTTP 500 Internal Server Error
 * "Connection timeout after 30000ms"
 *
 *
 * 🎯 AVANTAGES :
 * ==============
 * ✅ Message clair pour l'utilisateur
 * ✅ Code HTTP approprié (503 au lieu de 500)
 * ✅ Indique que c'est temporaire
 * ✅ Donne des suggestions
 * ✅ L'application ne crash pas
 * ✅ Logs clairs côté serveur
 *
 *
 * 🔧 COMMENT C'EST UTILISÉ ?
 * ==========================
 * Dans application.yml, on configure :
 *
 * spring:
 *   cloud:
 *     gateway:
 *       routes:
 *         - id: movie-service
 *           uri: lb://movie-service
 *           filters:
 *             - name: CircuitBreaker
 *               args:
 *                 name: movieServiceCircuitBreaker
 *                 fallbackUri: forward:/fallback/movie-service  👈 ICI !
 *
 * Quand le Circuit Breaker s'ouvre, il redirige vers ce controller.
 */