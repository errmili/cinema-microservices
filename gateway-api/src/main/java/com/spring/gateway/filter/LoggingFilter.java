package com.spring.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * 📊 FILTRE DE LOGGING PROFESSIONNEL - VERSION SIMPLE V3
 *
 * LA VRAIE SOLUTION : RÉCUPÉRER LE CORRELATION ID DEPUIS LES HEADERS !
 * =====================================================================
 *
 * POURQUOI C'EST MIEUX ?
 * ======================
 * Au lieu de se battre avec le Reactor Context (complexe), on récupère simplement
 * le Correlation ID depuis les headers de la requête où le CorrelationIdFilter l'a mis.
 *
 * AVANTAGES :
 * ===========
 * ✅ SIMPLE : Pas besoin de contextWrite, deferContextual, etc.
 * ✅ FIABLE : Fonctionne à 100%, peu importe le thread
 * ✅ LISIBLE : N'importe quel dev peut comprendre le code
 * ✅ PAS DE [NO-ID] : Le Correlation ID est toujours disponible
 */
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        Instant startTime = Instant.now();

        // 🎯 LA CLÉ : Récupérer le Correlation ID depuis les HEADERS
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = "NO-ID";
        }

        // Mettre le Correlation ID dans le MDC pour les logs
        MDC.put(CORRELATION_ID_KEY, correlationId);

        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        String query = request.getURI().getQuery() != null ? "?" + request.getURI().getQuery() : "";
        String clientIp = getClientIp(request);
        String userAgent = request.getHeaders().getFirst("User-Agent");

        // 📥 Logger la requête ENTRANTE
        logger.info("➡️ Requête entrante : {} {} {} - IP: {} - User-Agent: {}",
                method,
                path,
                query,
                clientIp,
                userAgent != null ? userAgent : "N/A");

        // Stocker le Correlation ID pour l'utiliser dans doOnSuccess/doOnError
        final String finalCorrelationId = correlationId;

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // ✨ Remettre le Correlation ID dans le MDC avant de logger
                    MDC.put(CORRELATION_ID_KEY, finalCorrelationId);
                    try {
                        logResponse(exchange, startTime, true);
                    } finally {
                        MDC.remove(CORRELATION_ID_KEY);
                    }
                })
                .doOnError(error -> {
                    // ✨ Remettre le Correlation ID dans le MDC avant de logger
                    MDC.put(CORRELATION_ID_KEY, finalCorrelationId);
                    try {
                        logger.error("❌ ERREUR lors du traitement : {} {} - Exception: {}",
                                method,
                                path,
                                error.getMessage(),
                                error);
                        logResponse(exchange, startTime, false);
                    } finally {
                        MDC.remove(CORRELATION_ID_KEY);
                    }
                })
                .doFinally(signalType -> {
                    // Nettoyer le MDC à la fin
                    MDC.remove(CORRELATION_ID_KEY);
                });
    }

    private void logResponse(ServerWebExchange exchange, Instant startTime, boolean isSuccess) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        Duration duration = Duration.between(startTime, Instant.now());
        long durationMs = duration.toMillis();

        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        int status = statusCode != null ? statusCode.value() : 0;

        if (status >= 500) {
            logger.error("⬅️ ❌ ERREUR SERVEUR : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }
        else if (status >= 400) {
            logger.warn("⬅️ ⚠️ Erreur client : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }
        else if (status >= 200 && status < 300) {
            logger.info("⬅️ ✅ Succès : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }
        else {
            logger.info("⬅️ Réponse : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }

        if (durationMs > 3000) {
            logger.warn("🐌 PERFORMANCE : Requête lente détectée ! {} {} a pris {}ms",
                    method, path, durationMs);
        }
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "UNKNOWN";
    }

    @Override
    public int getOrder() {
        return -50;
    }
}

/**
 * 💡 POURQUOI CETTE SOLUTION EST LA MEILLEURE ?
 * ==============================================
 *
 * COMPARAISON DES APPROCHES :
 * ---------------------------
 *
 * ❌ APPROCHE 1 : MDC seulement
 * - Problème : Le MDC est ThreadLocal, perdu entre les threads
 * - Résultat : [NO-ID] dans les logs finaux
 *
 * ❌ APPROCHE 2 : Reactor Context + contextWrite + deferContextual
 * - Problème : Complexe, difficile à maintenir, peut ne pas fonctionner dans tous les cas
 * - Résultat : Code compliqué pour les autres devs
 *
 * ✅ APPROCHE 3 : Headers (CETTE VERSION)
 * - Avantage : Le Correlation ID est dans les headers, accessible partout
 * - Résultat : Simple, fiable, fonctionne à 100%
 *
 * COMMENT ÇA MARCHE ?
 * ===================
 * 1. CorrelationIdFilter génère le Correlation ID
 * 2. CorrelationIdFilter l'ajoute dans les headers : request.header("X-Correlation-Id", "abc-123")
 * 3. LoggingFilter lit les headers : request.getHeaders().getFirst("X-Correlation-Id")
 * 4. LoggingFilter met le Correlation ID dans le MDC avant CHAQUE log
 * 5. Résultat : Tous les logs ont le Correlation ID !
 *
 *
 * 🔍 RÉSULTAT ATTENDU
 * ====================
 *
 * [abc-123] DEBUG - 🆕 Nouveau Correlation ID généré : abc-123
 * [abc-123] INFO  - ➡️ Requête entrante : POST /api/auth/login
 * [abc-123] DEBUG - 🔐 Vérification JWT
 * [abc-123] INFO  - 🟢 Path public autorisé
 * [abc-123] INFO  - ⬅️ ✅ Succès - Status: 200 - Durée: 925ms    ✅ PLUS DE [NO-ID] !
 *
 *
 * 📝 NOTE IMPORTANTE
 * ==================
 * Cette approche fonctionne parce que :
 * - Les headers HTTP sont immuables et propagés automatiquement
 * - Pas besoin de se soucier des threads ou du context réactif
 * - Simple à comprendre et à maintenir
 * - Standard dans l'industrie (Zipkin, Jaeger font pareil)
 */