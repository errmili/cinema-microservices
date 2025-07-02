package com.spring.management.usermanagement.api.controller;
import com.spring.management.usermanagement.api.dto.AuthRequest;
import com.spring.management.usermanagement.api.dto.AuthResponse;
import com.spring.management.usermanagement.api.dto.RegisterRequest;
import com.spring.management.usermanagement.application.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@CrossOrigin(
//        origins = {"http://localhost:4200", "http://localhost:3000"},
//        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
//        allowedHeaders = "*",
//        allowCredentials = "true"
//)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}