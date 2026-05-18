package com.spring.management.usermanagement.infrastructure.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    //Un filtre qui s'exécute AVANT chaque requête HTTP entrante pour vérifier le token JWT.
    //Si OK → mettre l'user dans le SecurityContext (= "user authentifié")
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    //Un service qui charge un utilisateur depuis ta BDD à partir de son username/email.
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/v2/api-docs",              // Documentation Swagger
                                "/v3/api-docs",              // Documentation Swagger
                                "/v3/api-docs/**",           // Documentation Swagger
                                "/swagger-resources",        // Swagger UI
                                "/swagger-resources/**",     // Swagger UI
                                "/configuration/ui",         // Swagger UI
                                "/configuration/security",   // Swagger UI
                                "/swagger-ui/**",            // Swagger UI
                                "/webjars/**",               // Swagger UI
                                "/swagger-ui.html"           // Swagger UI
                        )
                        .permitAll()
                      //  .requestMatchers("/reservations/v1/tickets/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //C'est le "vérificateur de login/password" de Spring Security : il sait comment authentifier un user en BDD
    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();  // ① Provider qui lit en BDD
        provider.setUserDetailsService(userDetailsService);  // ② COMMENT trouver le user
        provider.setPasswordEncoder(passwordEncoder());      // ③ COMMENT comparer le mot de passe
        return provider;
    }

    //C'est le "chef d'orchestre" de l'authentification : c'est lui que tu appelles directement dans ton code pour déclencher le login.
   //Pourquoi 2 beans (Manager + Provider) ? 🤔
    //Parce qu'une app peut avoir plusieurs façons d'authentifier
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}