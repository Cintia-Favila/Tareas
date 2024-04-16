package com.example.demo.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "El nombre de usuario no puede estar en blanco")
    String username;
    String email;
    @NotBlank(message = "La contraseña no puede estar en blanco")
    String password;
}
