package com.example.demo.controllers;
import com.example.demo.rabbitMQ.Producer;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.AuthResponse;
import com.example.demo.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private Producer producer;
    public AuthController(AuthService authService, Producer producer) {
        this.authService = authService;
        this.producer = producer;
    }

    @PostMapping(value = "register")
    public ResponseEntity<String> registerController(@Valid @RequestBody RegisterRequest request) {
        String authResponse = authService.register(request);
        producer.registerUser("New registered user, Username: " + request.getUsername());
        authService.sendNotification("Registro", "Te has registrado exitosamente");
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping(value = "login")
    public ResponseEntity<AuthResponse> loginController(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
