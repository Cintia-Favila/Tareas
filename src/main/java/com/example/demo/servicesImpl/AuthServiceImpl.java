package com.example.demo.servicesImpl;
import com.example.demo.jwt.JwtService;
import com.example.demo.models.UserModel;
import com.example.demo.repositories.UserJpaRepository;
import com.example.demo.request.LoginRequest;
import com.example.demo.request.RegisterRequest;
import com.example.demo.response.AuthResponse;
import com.example.demo.services.AuthService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserJpaRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public String register(RegisterRequest request) {
        Optional<UserModel> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            return "El nombre de usuario ya está en uso";
        }
        UserModel userModel = UserModel.builder()
                .username(request.getUsername())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
        userRepository.save(userModel);
        return "¡Registro exitoso para el usuario: " + request.getUsername() + "!";
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(userDetails);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public void sendNotification(String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        System.out.println("Enviando notificación:");
        System.out.println("  Título: " + title);
        System.out.println("  Cuerpo: " + body);

        Message message = Message.builder()
                .setNotification(notification)
                .setTopic("topic")
                .build();
        try {
            firebaseMessaging.send(message);
            System.out.println("Notificación enviada correctamente");
        } catch (FirebaseMessagingException e) {
            System.err.println("Error al enviar la notificación: " + e.getMessage());
        }
    }
}
