package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.LoginRequestDTO;
import com.example.EcommerceBackendProject.DTO.LoginResponseDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.ok(authService.register(userRequestDTO));
    }

}
