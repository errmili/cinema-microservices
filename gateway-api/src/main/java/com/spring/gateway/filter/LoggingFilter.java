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
// 💡 @Component sert à dire à Spring : "Crée automatiquement un objet de cette classe au démarrage"
//    Sans cette annotation, Spring ignorerait complètement cette classe
// 💡 GlobalFilter sert à intercepter TOUTES les requêtes qui passent par la gateway
//    Ordered sert à définir l'ordre d'exécution par rapport aux autres filtres
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    // 💡 Cela sert à écrire des messages de log proprement
    //    LoggerFactory.getLogger(LoggingFilter.class) → les logs seront identifiés
    //    avec le nom de cette classe, pratique pour les retrouver facilement
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    // 💡 Ces constantes servent à éviter les "magic strings" (chaînes écrites en dur partout)
    //    Si demain le nom du header change, on le modifie UNE SEULE FOIS ici → bonne pratique Senior !
    //    CORRELATION_ID_HEADER → le nom du header HTTP qu'on lit dans la requête
    //    CORRELATION_ID_KEY    → le nom de la clé dans le MDC (le système de log contextuel)
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_KEY = "correlationId";

    // 💡 C'est la méthode principale appelée automatiquement pour CHAQUE requête
    //    ServerWebExchange = contient toute la requête (headers, body, IP...) ET la réponse
    //    GatewayFilterChain = la chaîne des filtres → permet de passer au filtre suivant
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 💡 Cela sert à extraire l'objet requête depuis l'exchange
        //    On préfère le stocker dans une variable pour ne pas réécrire exchange.getRequest() partout
        ServerHttpRequest request = exchange.getRequest();

        // 💡 Instant.now() sert à capturer l'heure exacte d'arrivée de la requête
        //    On s'en servira plus tard pour calculer combien de temps a duré le traitement
        Instant startTime = Instant.now();

        // 🎯 LA CLÉ : Récupérer le Correlation ID depuis les HEADERS
        // 💡 Cela sert à récupérer l'ID unique de la requête posé par CorrelationIdFilter
        //    Si pour une raison quelconque il est absent, on met "NO-ID" comme valeur par défaut
        //    Grâce à cet ID, tous les logs de cette requête seront liés entre eux
        String correlationId = request.getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = "NO-ID";
        }

        // Mettre le Correlation ID dans le MDC pour les logs
        // 💡 MDC (Mapped Diagnostic Context) sert à ajouter des infos contextuelles à TOUS les logs
        //    Une fois qu'on fait MDC.put("correlationId", "abc-123"), chaque log écrit ensuite
        //    affichera automatiquement [abc-123] → c'est ce qu'on voit dans logback-spring.xml
        //    avec %X{correlationId}
        MDC.put(CORRELATION_ID_KEY, correlationId);

        // 💡 Ces lignes servent à extraire les informations utiles de la requête
        //    pour les logger et savoir exactement ce que le client demande
        String method = request.getMethod().name();   // → "GET", "POST", "PUT", "DELETE"
        String path = request.getURI().getPath();      // → "/api/movies/42"
        String query = request.getURI().getQuery() != null ? "?" + request.getURI().getQuery() : ""; // → "?page=1&size=10"
        String clientIp = getClientIp(request);        // → "192.168.1.1"
        String userAgent = request.getHeaders().getFirst("User-Agent"); // → "Mozilla/5.0..." ou "Postman"

        // 📥 Logger la requête ENTRANTE
        // 💡 Cela sert à tracer TOUTE requête qui arrive
        //    Exemple de log : "➡️ Requête entrante : GET /api/movies ?page=1 - IP: 192.168.1.1 - User-Agent: Mozilla"
        logger.info("➡️ Requête entrante : {} {} {} - IP: {} - User-Agent: {}",
                method,
                path,
                query,
                clientIp,
                userAgent != null ? userAgent : "N/A");

        // 💡 On stocke le correlationId dans une variable "final" car les lambdas
        //    (doOnSuccess, doOnError) ne peuvent pas utiliser des variables qui pourraient changer
        //    C'est une règle Java : les lambdas ne capturent que des variables "effectively final"
        final String finalCorrelationId = correlationId;

        // 💡 chain.filter(exchange) sert à passer la requête au filtre suivant (puis au microservice)
        //    Les méthodes .doOnSuccess / .doOnError / .doFinally s'exécutent APRÈS que le microservice répond
        //    C'est la programmation réactive (WebFlux) : on définit ce qui se passe "quand c'est fini"
        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // ✨ Remettre le Correlation ID dans le MDC avant de logger
                    // 💡 doOnSuccess s'exécute quand la requête s'est terminée SANS erreur
                    //    On remet le correlationId dans le MDC car dans un contexte réactif,
                    //    le thread peut avoir changé entre la requête et la réponse → le MDC est perdu
                    MDC.put(CORRELATION_ID_KEY, finalCorrelationId);
                    try {
                        logResponse(exchange, startTime, true); // 💡 On log la réponse (status, durée...)
                    } finally {
                        MDC.remove(CORRELATION_ID_KEY); // 💡 On nettoie le MDC après usage pour éviter les fuites mémoire
                    }
                })
                .doOnError(error -> {
                    // ✨ Remettre le Correlation ID dans le MDC avant de logger
                    // 💡 doOnError s'exécute quand une EXCEPTION s'est produite pendant le traitement
                    //    On log l'erreur avec tous les détails pour faciliter le débogage
                    MDC.put(CORRELATION_ID_KEY, finalCorrelationId);
                    try {
                        logger.error("❌ ERREUR lors du traitement : {} {} - Exception: {}",
                                method,
                                path,
                                error.getMessage(),
                                error); // 💡 On passe "error" en dernier pour avoir la stack trace complète dans les logs
                        logResponse(exchange, startTime, false);
                    } finally {
                        MDC.remove(CORRELATION_ID_KEY);
                    }
                })
                .doFinally(signalType -> {
                    // Nettoyer le MDC à la fin
                    // 💡 doFinally s'exécute TOUJOURS, que ce soit un succès ou une erreur
                    //    C'est la sécurité finale pour s'assurer que le MDC est bien nettoyé
                    //    Si on ne nettoie pas → le correlationId d'une requête peut "polluer" la suivante !
                    MDC.remove(CORRELATION_ID_KEY);
                });
    }

    // 💡 Cette méthode privée sert à logger les détails de la RÉPONSE
    //    Elle est appelée après que le microservice a répondu
    //    Elle calcule la durée et adapte le niveau de log selon le statut HTTP
    private void logResponse(ServerWebExchange exchange, Instant startTime, boolean isSuccess) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 💡 Duration.between sert à calculer le temps écoulé entre l'arrivée de la requête
        //    et maintenant → c'est le temps total de traitement vu par la gateway
        Duration duration = Duration.between(startTime, Instant.now());
        long durationMs = duration.toMillis(); // 💡 On convertit en millisecondes pour que ce soit lisible

        String method = request.getMethod().name();
        String path = request.getURI().getPath();
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();
        int status = statusCode != null ? statusCode.value() : 0; // 💡 Si pas de status, on met 0 par défaut

        // 💡 Ces conditions servent à choisir le bon niveau de log selon le code HTTP retourné
        //    C'est une bonne pratique : on n'utilise pas ERROR pour tout, on est précis
        if (status >= 500) {
            // 💡 5xx = erreur côté SERVEUR (notre faute) → ERROR, c'est grave
            logger.error("⬅️ ❌ ERREUR SERVEUR : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }
        else if (status >= 400) {
            // 💡 4xx = erreur côté CLIENT (mauvaise requête, non autorisé...) → WARN, moins grave
            logger.warn("⬅️ ⚠️ Erreur client : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }
        else if (status >= 200 && status < 300) {
            // 💡 2xx = succès → INFO, tout va bien
            logger.info("⬅️ ✅ Succès : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }
        else {
            // 💡 Autres cas (1xx redirections, 3xx...) → INFO générique
            logger.info("⬅️ Réponse : {} {} - Status: {} - Durée: {}ms",
                    method, path, status, durationMs);
        }

        // 💡 Cela sert à détecter les requêtes LENTES (plus de 3 secondes)
        //    C'est très utile en production pour identifier les problèmes de performance
        //    Un Senior surveille toujours les temps de réponse de ses services !
        if (durationMs > 3000) {
            logger.warn("🐌 PERFORMANCE : Requête lente détectée ! {} {} a pris {}ms",
                    method, path, durationMs);
        }
    }

    // 💡 Cette méthode sert à récupérer la vraie IP du client
    //    En production, les requêtes passent souvent par plusieurs couches (proxy, load balancer, CDN)
    //    avant d'arriver ici → l'IP directe ne serait pas la bonne
    private String getClientIp(ServerHttpRequest request) {
        // 💡 X-Forwarded-For : ajouté par les proxies/load balancers
        //    Peut contenir plusieurs IPs : "IP_client, IP_proxy1, IP_proxy2"
        //    → on prend toujours la première qui est la vraie IP du client
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // 💡 X-Real-IP : header alternatif utilisé par Nginx
        //    Contient directement l'IP du client sans liste
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // 💡 En dernier recours, on prend l'IP directe de la connexion TCP
        //    C'est fiable seulement s'il n'y a aucun proxy entre le client et la gateway
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "UNKNOWN"; // 💡 Si vraiment on ne peut pas trouver l'IP
    }

    /**
     * 💡 Cela sert à définir que ce filtre s'exécute EN DEUXIÈME
     *    Ordre d'exécution des filtres :
     *    1️⃣  CorrelationIdFilter (-100) → génère l'ID unique en premier
     *    2️⃣  LoggingFilter       (-50)  → log avec l'ID déjà disponible ✅
     *    3️⃣  AuthenticationFilter (-1)  → vérifie le JWT en dernier
     *
     *    Si LoggingFilter était avant CorrelationIdFilter,
     *    les logs n'auraient pas encore l'ID → beaucoup moins utile !
     */
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