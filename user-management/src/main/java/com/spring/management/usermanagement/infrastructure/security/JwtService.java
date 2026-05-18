package com.spring.management.usermanagement.infrastructure.security;

import com.spring.management.usermanagement.domain.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//C'est la classe qui gère toute l'authentification
//par token dans ton app Spring Boot.
@Service
public class JwtService {

    // Clé secrète chargée depuis application.properties
    @Value("${app.jwt.secret}")
    private String secretKey;

    //Durée de vie du token (en ms)
    @Value("${app.jwt.expirationMs}")
    private long expirationMs;

    // fait juste du debug temporaire
    //même dangereux en production car ça affiche ta secretKey
    //dans les logs ! Si quelqu'un accède aux logs → il peut forger des tokens.
    //// ❌ À SUPPRIMER (ou au moins commenter)
    @PostConstruct
    public void logProps() {
        System.out.println("✅ [PostConstruct] SecretKey: " + secretKey);// 🚨 FUITE DE SÉCURITÉ
        System.out.println("✅ [PostConstruct] Expiration: " + expirationMs);
    }

    //Convertit la secretKey en objet Key utilisable par JWT (HMAC-SHA)
    private Key getSigningKey() {
        System.out.println("🔐 Clé utilisée pour signer dans user-management : " + secretKey);

        return Keys.hmacShaKeyFor(

        secretKey.getBytes());
    }

    // Récupère le "subject" (username) du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    // Méthode générique : parse le token et extrait n'importe quel claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // Crée un JWT avec roles + email + userId + username + date expiration, signé HS256
    public String generateToken(User user) {
        // Extraire les rôles de l'utilisateur
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        // Créer les claims personnalisés
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("email", user.getEmail());
        claims.put("userId", user.getId());

        System.out.println("🎭 Génération token pour: " + user.getUsername());
        System.out.println("🎭 Rôles inclus: " + roles);

        return Jwts.builder()
                .setClaims(claims)  // ✅ IMPORTANT: Ajouter les claims AVANT setSubject
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    // Vérifie : username correspond ET token non expiré
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // True si la date d'expiration est passée
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // ✅ NOUVELLE MÉTHODE : Récupère la liste des rôles depuis le claim "roles"
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof List) {
                return (List<String>) roles;
            }
            return List.of();
        });
    }

    // ✅ NOUVELLE MÉTHODE : Extraire ou recuperer l'email du token depuis le claim "email"
    public String extractEmail(String token) {
        return extractClaim(token, claims -> (String) claims.get("email"));
    }
}