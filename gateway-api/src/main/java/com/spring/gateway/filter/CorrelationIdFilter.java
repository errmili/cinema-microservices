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
// 💡 @Component sert à dire à Spring : "Gère cette classe automatiquement"
//    Spring va créer un objet de cette classe au démarrage et l'utiliser pour chaque requête
// 💡 C'est LE PREMIER filtre qui s'exécute (order = -100)
//    Son rôle : donner une identité unique à chaque requête avant que les autres filtres travaillent
@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    // 💡 Logger pour tracer la création/réception des Correlation IDs
    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);

    // 💡 Ces constantes servent à nommer l'ID unique de la requête
    //    CORRELATION_ID_HEADER → le nom du header HTTP (visible dans la requête/réponse)
    //    CORRELATION_ID_KEY    → le nom de la clé dans le MDC (système de log contextuel)
    //    On les définit ici en constante pour ne pas écrire la chaîne en dur à plusieurs endroits
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_KEY = "correlationId";

    // 💡 C'est la méthode principale exécutée pour CHAQUE requête qui entre dans la gateway
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1️⃣ Récupérer ou générer le Correlation ID
        // 💡 On regarde d'abord si la requête a DÉJÀ un Correlation ID dans ses headers
        //    Cas où c'est déjà présent : un autre microservice ou le frontend l'a ajouté
        //    Cas où c'est absent : c'est une nouvelle requête qui vient du client directement
        String correlationId = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isEmpty()) {
            // 💡 Aucun ID trouvé → on en génère un nouveau avec UUID
            //    UUID = identifiant universel unique → ex: "a3f8c2d1-4b7e-4f9a-8c3d-1e2f3a4b5c6d"
            //    La probabilité d'avoir deux UUID identiques est astronomiquement faible
            correlationId = generateCorrelationId();
            logger.debug("🆕 Nouveau Correlation ID généré : {}", correlationId);
        } else {
            // 💡 Un ID existant a été trouvé → on le conserve tel quel
            //    C'est important pour tracer une action qui traverse PLUSIEURS microservices :
            //    service A appelle service B avec le même ID → on peut reconstituer le parcours complet
            logger.debug("🔄 Correlation ID existant reçu : {}", correlationId);
        }

        // 2️⃣ Mettre le Correlation ID dans le MDC pour le log immédiat
        // 💡 MDC (Mapped Diagnostic Context) = un espace de stockage propre à chaque thread
        //    En mettant l'ID ici, TOUS les logs écrits ensuite afficheront automatiquement cet ID
        //    C'est ce qui fait apparaître [abc-123] dans chaque ligne de log (configuré dans logback-spring.xml)
        final String finalCorrelationId = correlationId;
        // 💡 "final" est obligatoire ici car on va utiliser cette variable dans des lambdas plus bas
        //    Java exige que les variables utilisées dans les lambdas ne changent pas
        MDC.put(CORRELATION_ID_KEY, finalCorrelationId);

        // 3️⃣ Ajouter le Correlation ID dans la requête sortante
        // 💡 .mutate() sert à créer une COPIE MODIFIÉE de la requête (les objets Spring sont immuables)
        //    On ne peut pas modifier directement une requête → on en crée une nouvelle avec l'ID ajouté
        //    Ainsi, quand la requête arrivera au microservice cible, il aura l'ID dans ses headers
        ServerHttpRequest modifiedRequest = exchange.getRequest()
                .mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId) // 💡 On injecte l'ID dans le header
                .build();

        // 4️⃣ Créer un nouvel exchange avec la requête modifiée
        // 💡 ServerWebExchange contient TOUT (requête + réponse)
        //    On crée un nouvel exchange qui utilise notre requête modifiée (avec l'ID)
        //    C'est ce nouvel exchange qu'on passera aux filtres suivants
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(modifiedRequest)
                .build();

        // 5️⃣ Ajouter le Correlation ID dans la réponse
        // 💡 On ajoute aussi l'ID dans la RÉPONSE HTTP retournée au client
        //    Pourquoi ? → Si le client a un bug, il peut nous donner cet ID
        //    et on retrouve EXACTEMENT sa requête dans tous nos logs
        //    C'est très utile en support technique !
        modifiedExchange.getResponse()
                .getHeaders()
                .add(CORRELATION_ID_HEADER, finalCorrelationId);

        // 6️⃣ 🎯 LA CLÉ : Propager le Correlation ID dans le Reactor Context
        // 💡 chain.filter(modifiedExchange) = on passe au filtre suivant avec notre exchange enrichi
        return chain.filter(modifiedExchange)
                // ✨ NOUVEAU : Mettre à jour le MDC à chaque signal réactif
                // 💡 .doOnEach() sert à exécuter du code à chaque "signal" du flux réactif
                //    En programmation réactive (WebFlux), le thread peut changer à tout moment
                //    → le MDC (qui est lié au thread) peut se vider entre deux opérations
                //    → on le recharge à chaque signal pour ne jamais perdre l'ID dans les logs
                .doOnEach(signal -> {
                    // 💡 On récupère l'ID depuis le Context réactif (pas depuis le MDC)
                    //    car le Context réactif lui, survit aux changements de thread
                    String ctxCorrelationId = signal.getContextView().getOrDefault(CORRELATION_ID_KEY, "NO-ID");
                    MDC.put(CORRELATION_ID_KEY, ctxCorrelationId);
                })
                // ✨ NOUVEAU : Propager le correlationId dans le Context réactif
                // 💡 .contextWrite() sert à stocker l'ID dans le Context de Reactor
                //    Le Context réactif = un espace de stockage qui SURVIT aux changements de thread
                //    C'est la solution au problème du MDC en WebFlux :
                //    MDC      → lié au thread → peut se perdre ❌
                //    Context  → lié au flux   → toujours disponible ✅
                .contextWrite(Context.of(CORRELATION_ID_KEY, finalCorrelationId))
                // Nettoyer le MDC à la fin
                // 💡 .doFinally() s'exécute TOUJOURS en dernier (succès ou erreur)
                //    On nettoie le MDC pour éviter que l'ID d'une requête
                //    "contamine" la prochaine requête qui utiliserait le même thread
                .doFinally(signalType -> MDC.remove(CORRELATION_ID_KEY));
    }

    // 💡 Cette méthode sert à générer un identifiant UNIQUE pour chaque requête
    //    UUID.randomUUID() produit un ID de type : "a3f8c2d1-4b7e-4f9a-8c3d-1e2f3a4b5c6d"
    //    32 caractères hexadécimaux → pratiquement impossible d'avoir deux fois le même
    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 💡 Cela sert à définir que ce filtre s'exécute EN PREMIER, avant tous les autres
     *    -100 est le nombre le plus petit → priorité maximale
     *
     *    Pourquoi en premier ? Car les autres filtres ont besoin de l'ID pour leurs logs :
     *    1️⃣  CorrelationIdFilter (-100) → génère et injecte l'ID            ← ON EST ICI
     *    2️⃣  LoggingFilter       (-50)  → utilise l'ID pour ses logs        ✅
     *    3️⃣  AuthenticationFilter (-1)  → utilise l'ID pour ses logs        ✅
     *
     *    Si on inversait l'ordre, les logs des autres filtres n'auraient pas d'ID → beaucoup moins utile !
     */
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