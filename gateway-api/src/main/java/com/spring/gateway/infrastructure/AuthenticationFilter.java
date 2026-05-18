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



// 🎯 Cette classe sert à protéger toutes les routes de la gateway
// Elle joue le rôle d'un VIDEUR : elle vérifie si tu as le droit d'entrer avant de te laisser passer
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    // 📝 Logger professionnel - à utiliser TOUJOURS au lieu de System.out.println
    // 💡 Cela sert à écrire des messages dans la console ou dans un fichier de log,
    //    pour savoir ce qui se passe dans l'application en temps réel
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    // 💡 Cela sert à récupérer la clé secrète depuis le fichier application.yml
    //    Cette clé est utilisée pour vérifier que le token JWT est authentique (pas falsifié)
    @Value("${app.jwt.secret}")
    private String secretKey;

    // 💡 C'est la méthode principale : elle est appelée automatiquement à CHAQUE requête qui arrive
    //    C'est ici que tout le travail de vérification se fait
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        // 💡 Cela sert à récupérer l'URL que le client veut atteindre
        //    Exemple : /api/users/42 ou /api/auth/login
        String path = exchange.getRequest().getURI().getPath();

        // 🔍 Log DEBUG : utile en dev, désactivé en prod
        logger.debug("🔐 Vérification JWT pour : {}", path);

        // ✅ Laisser passer sans token pour /api/auth/** (login, register, etc.)
        // 💡 Cela sert à autoriser les routes publiques (login, register) sans vérifier le token
        //    Normal : quand tu te connectes pour la 1ère fois, tu n'as pas encore de token !
        if (path.startsWith("/api/auth/")) {
            logger.info("🟢 Path public autorisé sans JWT : {}", path);
            return chain.filter(exchange); // 💡 "chain.filter" = passe au filtre suivant / laisse entrer
        }

        // 🔎 Vérifier Authorization header
        // 💡 Cela sert à lire le header "Authorization" envoyé par le client
        //    Le client doit envoyer : Authorization: Bearer eyJhbGci...
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 💡 Si le header est absent ou ne commence pas par "Bearer ", on refuse l'accès
        //    C'est comme se présenter à la boîte sans ta carte d'entrée
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // ⚠️ WARN : Problème côté client (token manquant)
            logger.warn("⚠️ Token JWT manquant pour : {} - IP: {}",
                    path,
                    getClientIp(exchange));

            // 💡 On répond avec le code HTTP 401 = "Non autorisé, identifie-toi d'abord"
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // 💡 On arrête tout, on renvoie la réponse au client
        }

        // 💡 Cela sert à extraire uniquement le token JWT en supprimant le préfixe "Bearer "
        //    Avant : "Bearer eyJhbGci..."  →  Après : "eyJhbGci..."
        String token = authHeader.substring(7);

        // 🔍 Log DEBUG : on log le token seulement en dev (pas en prod pour la sécurité !)
        if (logger.isDebugEnabled()) {
            // 💡 On affiche seulement les 20 premiers caractères du token pour ne pas l'exposer en entier
            logger.debug("🪪 Token reçu : {}...", token.substring(0, Math.min(20, token.length())));
        }

        try {
            // 🔓 Vérifier et décoder le token
            // 💡 Cela sert à décoder le token JWT et vérifier sa signature avec notre clé secrète
            //    Si quelqu'un a falsifié le token, cette ligne va lever une exception
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // 💡 Cela sert à extraire le nom d'utilisateur contenu dans le token
            //    Le "subject" d'un JWT c'est généralement l'identifiant de l'utilisateur
            String username = claims.getSubject();

            // 💡 Cela sert à extraire le rôle de l'utilisateur (ADMIN, USER, etc.)
            //    Ce rôle peut être utilisé plus loin pour autoriser ou interdire certaines actions
            String role = claims.get("role", String.class);

            // ✅ Token valide - Log INFO
            logger.info("✅ JWT valide - User: {} - Role: {} - Path: {}",
                    username,
                    role != null ? role : "N/A",
                    path);

            // 🎁 BONUS : Ajouter les infos du user dans la requête (pour les microservices)
            // 💡 Cela sert à transmettre les infos du user aux autres microservices via des headers
            //    Comme ça, le service-user ou service-order saura QUI fait la requête
            //    sans avoir à re-décoder le token eux-mêmes → Très pratique !
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Id", username)   // 💡 Le microservice reçoit l'ID de l'utilisateur
                            .header("X-User-Role", role != null ? role : "USER") // 💡 Et son rôle
                    )
                    .build();

            return chain.filter(modifiedExchange); // 💡 On laisse passer la requête enrichie

        } catch (JwtException e) {
            // ⚠️ WARN : Token invalide (expiré, signature incorrecte, etc.)
            // 💡 Ce catch sert à gérer les erreurs liées au token JWT
            //    Exemple : token expiré, token modifié, mauvaise signature
            //    → On renvoie un 401 : "Ton token n'est pas valide"
            logger.warn("⚠️ JWT invalide pour {} - Raison: {} - IP: {}",
                    path,
                    e.getMessage(),
                    getClientIp(exchange));

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();

        } catch (Exception e) {
            // 🔴 ERROR : Erreur inattendue (ça, c'est grave !)
            // 💡 Ce catch sert à gérer toute autre erreur imprévue (bug dans le code, problème réseau, etc.)
            //    → On renvoie un 500 : "Le serveur a planté, c'est notre faute"
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
     * 💡 Cette méthode sert à trouver la vraie IP du client
     *    Si le client passe par un proxy ou un load balancer,
     *    l'IP réelle est dans le header "X-Forwarded-For"
     *    Sinon on prend l'IP directe de la connexion
     */
    private String getClientIp(ServerWebExchange exchange) {
        // 💡 X-Forwarded-For : header ajouté par les proxies/load balancers
        //    Il peut contenir plusieurs IPs séparées par virgule → on prend la première (la vraie)
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // 💡 Si pas de proxy, on récupère l'IP directement depuis la connexion TCP
        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        return "UNKNOWN"; // 💡 En dernier recours, on ne sait pas d'où vient la requête
    }

    /**
     * 🎯 ORDRE D'EXÉCUTION
     * -1 : Après CorrelationIdFilter (-100) et LoggingFilter (-50)
     * Comme ça, tous les logs d'authentification ont déjà le Correlation ID !
     *
     * 💡 Cela sert à définir dans quel ordre les filtres s'exécutent
     *    Plus le chiffre est petit (négatif), plus le filtre s'exécute TÔT
     *    Ordre ici :
     *      1️⃣  CorrelationIdFilter (-100) → génère l'ID unique
     *      2️⃣  LoggingFilter       (-50)  → log la requête
     *      3️⃣  AuthenticationFilter (-1)  → vérifie le token JWT
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