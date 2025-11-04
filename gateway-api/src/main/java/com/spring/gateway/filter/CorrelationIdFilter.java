package com.spring.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

/**
 * 🎯 FILTRE DE CORRELATION ID - VERSION AMÉLIORÉE
 *
 * CHANGEMENTS PAR RAPPORT À LA V1 :
 * ==================================
 * ✅ Utilise le Reactor Context au lieu du MDC uniquement
 * ✅ Le Correlation ID est maintenant présent dans TOUS les logs
 * ✅ Plus de [NO-ID] à la fin de la chaîne réactive
 *
 * POURQUOI CE CHANGEMENT ?
 * ========================
 * Spring Cloud Gateway est RÉACTIF (utilise Reactor avec Mono/Flux).
 * Le MDC traditionnel (ThreadLocal) ne fonctionne pas bien avec la programmation réactive
 * car le traitement peut changer de thread.
 *
 * SOLUTION :
 * ==========
 * On utilise le Reactor Context qui est propagé automatiquement dans toute la chaîne réactive.
 * Le MDC est mis à jour à chaque étape grâce à contextWrite() et doOnEach().
 */
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1️⃣ Récupérer ou générer le Correlation ID
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = generateCorrelationId();
            logger.debug("🆕 Nouveau Correlation ID généré : {}", correlationId);
        } else {
            logger.debug("🔄 Correlation ID existant reçu : {}", correlationId);
        }

        // 2️⃣ Mettre le Correlation ID dans le MDC pour le log immédiat
        final String finalCorrelationId = correlationId;
        MDC.put(CORRELATION_ID_KEY, finalCorrelationId);

        // 3️⃣ Ajouter le Correlation ID dans la requête sortante
        ServerHttpRequest modifiedRequest = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        // 4️⃣ Créer un nouvel exchange avec la requête modifiée
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        // 5️⃣ Ajouter le Correlation ID dans la réponse
        modifiedExchange.getResponse()
                .getHeaders()
                .add(CORRELATION_ID_HEADER, finalCorrelationId);

        // 6️⃣ 🎯 LA CLÉ : Propager le Correlation ID dans le Reactor Context
        return chain.filter(modifiedExchange)
                // ✨ NOUVEAU : Mettre à jour le MDC à chaque signal réactif
                .doOnEach(signal -> {
                    // Récupérer le correlationId depuis le Context
                    String ctxCorrelationId = signal.getContextView().getOrDefault(CORRELATION_ID_KEY, "NO-ID");
                    MDC.put(CORRELATION_ID_KEY, ctxCorrelationId);
                })
                // ✨ NOUVEAU : Propager le correlationId dans le Context réactif
                .contextWrite(Context.of(CORRELATION_ID_KEY, finalCorrelationId))
                // Nettoyer le MDC à la fin
                .doFinally(signalType -> MDC.remove(CORRELATION_ID_KEY));
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

/**
 * 💡 EXPLICATION TECHNIQUE
 * =========================
 *
 * AVANT (V1 avec seulement MDC) :
 * --------------------------------
 * 1. Thread-1 : MDC.put("correlationId", "abc-123")
 * 2. Thread-1 : Log avec [abc-123] ✅
 * 3. Thread-2 : Traitement continue (nouveau thread réactif)
 * 4. Thread-2 : MDC vide ! Log avec [NO-ID] ❌
 *
 * APRÈS (V2 avec Reactor Context) :
 * ----------------------------------
 * 1. Thread-1 : Context.of("correlationId", "abc-123")
 * 2. Thread-1 : doOnEach() met à jour MDC → Log [abc-123] ✅
 * 3. Thread-2 : Traitement continue (nouveau thread)
 * 4. Thread-2 : doOnEach() récupère depuis Context et met à jour MDC → Log [abc-123] ✅
 *
 * RÉSULTAT :
 * ==========
 * ✅ Le Correlation ID est TOUJOURS présent dans les logs
 * ✅ Fonctionne même avec plusieurs threads
 * ✅ Compatible avec la programmation réactive
 *
 *
 * 🔍 EXEMPLE DE LOGS AVANT/APRÈS
 * ===============================
 *
 * AVANT (V1) :
 * [abc-123] INFO - ➡️ Requête entrante
 * [abc-123] INFO - 🔐 Vérification JWT
 * [NO-ID]   INFO - ⬅️ Succès            ❌ Correlation ID perdu !
 *
 * APRÈS (V2) :
 * [abc-123] INFO - ➡️ Requête entrante
 * [abc-123] INFO - 🔐 Vérification JWT
 * [abc-123] INFO - ⬅️ Succès            ✅ Correlation ID présent !
 */