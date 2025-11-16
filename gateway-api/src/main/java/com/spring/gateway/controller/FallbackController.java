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
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    /**
     * 🎬 FALLBACK MOVIE SERVICE
     * Appelé quand movie-service est DOWN
     */
    @GetMapping("/movie-service")
    @PostMapping("/movie-service")
    @PutMapping("/movie-service")
    @DeleteMapping("/movie-service")
    public Mono<ResponseEntity<Map<String, Object>>> movieServiceFallback() {
        logger.warn("🎬 ⚠️ FALLBACK activé pour MOVIE-SERVICE");

        return Mono.just(ResponseEntity
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
    private Map<String, Object> createFallbackResponse(String message, String serviceName) {
        Map<String, Object> response = new HashMap<>();

        // Informations principales
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Service Unavailable");
        response.put("message", message);

        // Métadonnées utiles
        response.put("fallback", true);  // Indique que c'est une réponse fallback
        response.put("service", serviceName);
        response.put("timestamp", LocalDateTime.now().toString());

        // 💡 BONUS : Suggestions pour l'utilisateur
        response.put("suggestion", "Veuillez patienter quelques instants et réessayer");

        // 💡 BONUS : Support (en production, mettre vraie URL)
        response.put("support", "Si le problème persiste, contactez le support");

        return response;
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