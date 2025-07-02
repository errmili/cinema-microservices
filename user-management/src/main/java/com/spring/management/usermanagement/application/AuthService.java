package com.spring.management.usermanagement.application;
import com.spring.management.usermanagement.api.dto.AuthResponse;
import com.spring.management.usermanagement.domain.entity.User;
import com.spring.management.usermanagement.domain.entity.Role;
import com.spring.management.usermanagement.api.dto.AuthRequest;
import com.spring.management.usermanagement.api.dto.RegisterRequest;
import com.spring.management.usermanagement.domain.repository.RoleRepository;
import com.spring.management.usermanagement.domain.repository.UserRepository;
import com.spring.management.usermanagement.infrastructure.security.JwtService;

import lombok.RequiredArgsConstructor;

import java.util.Collections;

import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;



public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
    }
    public AuthResponse register(RegisterRequest request) {
        Role userRole = roleRepository.findByName("ROLE_USER") // Cherche le rôle dans la base de données
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singleton(userRole)) // Associe le rôle trouvé
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken((User) user);
        return AuthResponse.builder().token(token).build();
    }
}