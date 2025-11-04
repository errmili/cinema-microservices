package com.spring.gateway.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;

import java.nio.charset.StandardCharsets;

/**
 * 🔐 FILTRE D'AUTHENTIFICATION JWT - VERSION PRO
 *
 * CHANGEMENTS PAR RAPPORT À L'ANCIENNE VERSION :
 * ==============================================
 * ❌ AVANT : System.out.println() - MAUVAISE PRATIQUE !
 * ✅ APRÈS : Logger SLF4J - PRATIQUE PROFESSIONNELLE !
 *
 * POURQUOI SLF4J AU LIEU DE System.out.println ?
 * ==============================================
 * 1. System.out.println n'a pas de niveau (INFO, WARN, ERROR)
 * 2. System.out.println ne peut pas être désactivé en production
 * 3. System.out.println n'a pas de timestamp automatique
 * 4. System.out.println ne s'intègre pas avec les outils de monitoring (ELK, Splunk)
 * 5. System.out.println ne profite pas du Correlation ID (MDC)
 *
 * AVEC SLF4J :
 * ✅ Niveaux de log (DEBUG, INFO, WARN, ERROR)
 * ✅ Configuration par environnement (dev vs prod)
 * ✅ Timestamp + Correlation ID automatique
 * ✅ Intégration avec outils professionnels
 * ✅ Performance meilleure
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // 📝 Logger professionnel - à utiliser TOUJOURS au lieu de System.out.println
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // 🔍 Log DEBUG : utile en dev, désactivé en prod
        logger.debug("🔐 Vérification JWT pour : {}", path);

        // ✅ Laisser passer sans token pour /api/auth/** (login, register, etc.)
        if (path.startsWith("/api/auth/")) {
            logger.info("🟢 Path public autorisé sans JWT : {}", path);
            return chain.filter(exchange);
        }

        // 🔎 Vérifier Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // ⚠️ WARN : Problème côté client (token manquant)
            logger.warn("⚠️ Token JWT manquant pour : {} - IP: {}",
                    path,
                    getClientIp(exchange));

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        // 🔍 Log DEBUG : on log le token seulement en dev (pas en prod pour la sécurité !)
        if (logger.isDebugEnabled()) {
            logger.debug("🪪 Token reçu : {}...", token.substring(0, Math.min(20, token.length())));
        }

        try {
            // 🔓 Vérifier et décoder le token
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // ✅ Token valide - Log INFO
            logger.info("✅ JWT valide - User: {} - Role: {} - Path: {}",
                    username,
                    role != null ? role : "N/A",
                    path);

            // 🎁 BONUS : Ajouter les infos du user dans la requête (pour les microservices)
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Id", username)
                            .header("X-User-Role", role != null ? role : "USER")
                    )
                    .build();

            return chain.filter(modifiedExchange);

        } catch (JwtException e) {
            // ⚠️ WARN : Token invalide (expiré, signature incorrecte, etc.)
            logger.warn("⚠️ JWT invalide pour {} - Raison: {} - IP: {}",
                    path,
                    e.getMessage(),
                    getClientIp(exchange));

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();

        } catch (Exception e) {
            // 🔴 ERROR : Erreur inattendue (ça c'est grave !)
            logger.error("❌ ERREUR inattendue lors de la validation JWT pour {} - Exception: {}",
                    path,
                    e.getMessage(),
                    e);

            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }

    /**
     * Récupérer l'IP du client (même logique que LoggingFilter)
     */
    private String getClientIp(ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        return "UNKNOWN";
    }

    /**
     * 🎯 ORDRE D'EXÉCUTION
     * -1 : Après CorrelationIdFilter (-100) et LoggingFilter (-50)
     * Comme ça, tous les logs d'authentification ont déjà le Correlation ID !
     */
    @Override
    public int getOrder() {
        return -1;
    }
}

/**
 * 💡 DIFFÉRENCES AVANT/APRÈS
 * ===========================
 *
 * ❌ AVANT (avec System.out.println) :
 * ➡️ Requête interceptée sur le path : /movies/v1/1
 * 🪪 Token reçu : eyJhbGciOiJIUzI1NiIsInR5cCI6...
 * 🔑 Clé utilisée pour le parsing : LnX1yH3pYq6gTbWxV2zQ8uRfNjKrI0VxTk6b2o4D4q9w2U5QjX
 * ✅ Token valide pour l'utilisateur : john@example.com
 *
 * PROBLÈMES :
 * - Pas de timestamp
 * - Pas de Correlation ID
 * - Pas de niveau (INFO, WARN, ERROR)
 * - Pas de contexte (IP, path)
 * - Token complet visible (risque sécurité)
 *
 *
 * ✅ APRÈS (avec Logger SLF4J) :
 * [abc-123] 2025-10-27 14:30:12.156 INFO  - 🔐 Vérification JWT pour : /movies/v1/1
 * [abc-123] 2025-10-27 14:30:12.167 INFO  - ✅ JWT valide - User: john@example.com - Role: USER - Path: /movies/v1/1
 *
 * AVANTAGES :
 * ✅ Timestamp précis
 * ✅ Correlation ID [abc-123] - on peut tracer toute la requête
 * ✅ Niveau INFO (peut être désactivé en prod)
 * ✅ Contexte complet (user, role, path)
 * ✅ Token caché en prod (sécurité)
 *
 *
 * 🔍 EXEMPLE EN CAS D'ERREUR :
 * [def-456] 2025-10-27 14:35:45.234 WARN - ⚠️ JWT invalide pour /movies/v1/1 - Raison: JWT expired - IP: 192.168.1.100
 *
 * Tu vois immédiatement :
 * - Quelle requête (Correlation ID)
 * - Quel endpoint (/movies/v1/1)
 * - Pourquoi ça a échoué (expired)
 * - D'où ça vient (IP)
 */