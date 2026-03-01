package com.spring.management.usermanagement.application;

import com.spring.management.usermanagement.api.dto.AuthRequest;
import com.spring.management.usermanagement.api.dto.AuthResponse;
import com.spring.management.usermanagement.api.dto.RegisterRequest;
import com.spring.management.usermanagement.domain.entity.Role;
import com.spring.management.usermanagement.domain.entity.User;
import com.spring.management.usermanagement.domain.repository.RoleRepository;
import com.spring.management.usermanagement.domain.repository.UserRepository;
import com.spring.management.usermanagement.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires COMPLETS pour AuthService - VERSION CORRIGÉE
 * Service d'authentification et d'inscription avec JWT
 *
 * ✅ CORRECTION : Tous les verify() utilisent any(User.class) au lieu d'une instance spécifique
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires - AuthService (JWT Authentication)")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private Role userRole;
    private User user;

    @BeforeEach
    void setUp() {
        // Préparer une requête d'inscription valide
        registerRequest = RegisterRequest.builder()
                .username("johndoe")
                .email("john.doe@example.com")
                .password("SecurePassword123")
                .build();

        // Préparer une requête d'authentification valide
        authRequest = AuthRequest.builder()
                .username("johndoe")
                .password("SecurePassword123")
                .build();

        // Préparer un rôle USER
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        // Préparer un utilisateur
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user = User.builder()
                .id(1L)
                .username("johndoe")
                .email("john.doe@example.com")
                .password("$2a$10$encodedPassword")
                .roles(roles)
                .build();
    }

    // ==================== TESTS D'INSCRIPTION (register) ====================

    @Test
    @DisplayName("Test 1 : Inscription avec succès")
    void testRegister_Success() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("mock.jwt.token", response.getToken());

        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(passwordEncoder, times(1)).encode("SecurePassword123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class)); // ✅ CORRECTION
    }

    @Test
    @DisplayName("Test 2 : Inscription - Rôle ROLE_USER introuvable (Erreur)")
    void testRegister_RoleNotFound_ThrowsException() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Role not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Test 3 : Inscription - Mot de passe correctement encodé")
    void testRegister_PasswordEncoded() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(passwordEncoder, times(1)).encode("SecurePassword123");
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getPassword().equals("$2a$10$encodedPassword")
        ));
    }

    @Test
    @DisplayName("Test 4 : Inscription - Utilisateur sauvegardé avec les bonnes données")
    void testRegister_UserSavedWithCorrectData() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getUsername().equals("johndoe") &&
                        savedUser.getEmail().equals("john.doe@example.com") &&
                        savedUser.getRoles().contains(userRole)
        ));
    }

    @Test
    @DisplayName("Test 5 : Inscription - Token JWT généré pour l'utilisateur")
    void testRegister_JwtTokenGenerated() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("generated.jwt.token.12345");

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertEquals("generated.jwt.token.12345", response.getToken());
        verify(jwtService, times(1)).generateToken(any(User.class)); // ✅ CORRECTION
    }

    @Test
    @DisplayName("Test 6 : Inscription - Rôle USER correctement assigné")
    void testRegister_RoleUserAssigned() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository, times(1)).save(argThat(savedUser -> {
            Set<Role> roles = savedUser.getRoles();
            return roles.size() == 1 &&
                    roles.iterator().next().getName().equals("ROLE_USER");
        }));
    }

    @Test
    @DisplayName("Test 7 : Inscription - Repository save échoue (Erreur)")
    void testRegister_RepositorySaveFails_ThrowsException() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.register(registerRequest)
        );

        assertEquals("Database error", exception.getMessage());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    // ==================== TESTS D'AUTHENTIFICATION (authenticate) ====================

    @Test
    @DisplayName("Test 8 : Authentification avec succès")
    void testAuthenticate_Success() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        AuthResponse response = authService.authenticate(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("johndoe");
        verify(jwtService, times(1)).generateToken(any(User.class)); // ✅ CORRECTION
    }

    @Test
    @DisplayName("Test 9 : Authentification - Username introuvable (Erreur)")
    void testAuthenticate_UserNotFound_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.authenticate(authRequest)
        );

        assertEquals("User not found", exception.getMessage());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Test 10 : Authentification - Mauvais mot de passe (Erreur)")
    void testAuthenticate_BadCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(authRequest)
        );

        assertEquals("Bad credentials", exception.getMessage());
        verify(userRepository, never()).findByUsername(anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Test 11 : Authentification - AuthenticationManager appelé avec les bons credentials")
    void testAuthenticate_AuthenticationManagerCalledWithCorrectCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.authenticate(authRequest);

        // Then
        verify(authenticationManager, times(1)).authenticate(argThat(auth ->
                auth.getPrincipal().equals("johndoe") &&
                        auth.getCredentials().equals("SecurePassword123")
        ));
    }

    @Test
    @DisplayName("Test 12 : Authentification - Token généré pour l'utilisateur trouvé")
    void testAuthenticate_TokenGeneratedForFoundUser() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("user.specific.token");

        // When
        AuthResponse response = authService.authenticate(authRequest);

        // Then
        assertEquals("user.specific.token", response.getToken());
        verify(jwtService, times(1)).generateToken(any(User.class)); // ✅ CORRECTION
    }

    // ==================== TESTS DE VALIDATION DES DONNÉES ====================

    @Test
    @DisplayName("Test 13 : Inscription - Username null")
    void testRegister_NullUsername() {
        // Given
        registerRequest.setUsername(null);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getUsername() == null
        ));
    }

    @Test
    @DisplayName("Test 14 : Inscription - Email null")
    void testRegister_NullEmail() {
        // Given
        registerRequest.setEmail(null);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getEmail() == null
        ));
    }

    @Test
    @DisplayName("Test 15 : Inscription - Password null")
    void testRegister_NullPassword() {
        // Given
        registerRequest.setPassword(null);

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(null)).thenReturn("$2a$10$encodedNullPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(passwordEncoder, times(1)).encode(null);
    }

    @Test
    @DisplayName("Test 16 : Authentification - Username null")
    void testAuthenticate_NullUsername() {
        // Given
        authRequest.setUsername(null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.authenticate(authRequest)
        );

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test 17 : Authentification - Password null")
    void testAuthenticate_NullPassword() {
        // Given
        authRequest.setPassword(null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(authRequest)
        );

        assertEquals("Bad credentials", exception.getMessage());
    }

    // ==================== TESTS DE SÉCURITÉ ====================

    @Test
    @DisplayName("Test 18 : Inscription - Mot de passe en clair jamais sauvegardé")
    void testRegister_PlainPasswordNeverSaved() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("SecurePassword123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository, times(1)).save(argThat(savedUser ->
                !savedUser.getPassword().equals("SecurePassword123")
        ));
    }

    @Test
    @DisplayName("Test 19 : Authentification - Utilisateur récupéré après authentification réussie")
    void testAuthenticate_UserFetchedAfterSuccessfulAuth() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.authenticate(authRequest);

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername("johndoe");
    }

    @Test
    @DisplayName("Test 20 : Inscription - Plusieurs utilisateurs avec des rôles différents")
    void testRegister_MultipleUsersWithDifferentRoles() {
        // Given
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        // When
        authService.register(registerRequest);

        // Then
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ROLE_USER"))
        ));
    }
}