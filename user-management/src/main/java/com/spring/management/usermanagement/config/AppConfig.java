package com.spring.management.usermanagement.config;

import com.spring.management.usermanagement.application.AuthService;
import com.spring.management.usermanagement.domain.repository.RoleRepository;
import com.spring.management.usermanagement.domain.repository.UserRepository;
import com.spring.management.usermanagement.infrastructure.security.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public AuthService authService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtService jwtService,
                                   AuthenticationManager authenticationManager,
                                   RoleRepository roleRepository) {
        return new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                roleRepository
        );
    }
}