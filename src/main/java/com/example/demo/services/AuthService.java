package com.example.demo.services;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    

    void sendNotification(String title, String body);

    String register(RegisterRequest request);
}
