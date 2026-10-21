package com.leansofx.qaserviceuser.controller;

import com.leansofx.qaserviceuser.dto.PatientAuthDtos.LoginRequest;
import com.leansofx.qaserviceuser.dto.PatientAuthDtos.PatientUserResponse;
import com.leansofx.qaserviceuser.dto.PatientAuthDtos.RegisterRequest;
import com.leansofx.qaserviceuser.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<PatientUserResponse> register(@RequestBody RegisterRequest request, HttpSession session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, session));
    }

    @PostMapping("/login")
    public PatientUserResponse login(@RequestBody LoginRequest request, HttpSession session) {
        return authService.login(request, session);
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        return Map.of("message", authService.logout(session));
    }

    @GetMapping("/me")
    public PatientUserResponse me(HttpSession session) {
        return authService.currentUser(session);
    }
}
