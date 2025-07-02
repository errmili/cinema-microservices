package com.spring.gateway.infrastructure;

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

import java.nio.charset.StandardCharsets;

@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        System.out.println("➡️ Requête interceptée sur le path : " + path);

        // Laisser passer sans token pour /api/auth/**
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        // Vérifier Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        System.out.println("🪪 Token reçu : " + token); // Ajoute cette ligne pour debug
        System.out.println("🔑 Clé utilisée pour le parsing : " + secretKey);  // 🟡 Ajoute cette ligne
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
            System.out.println("✅ Token valide pour l'utilisateur : " + claims.getSubject());
            // Token valide
            return chain.filter(exchange);
        } catch (Exception e) {
            System.out.println("❌ Erreur de parsing du token : " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1; // Prioritaire
    }
}