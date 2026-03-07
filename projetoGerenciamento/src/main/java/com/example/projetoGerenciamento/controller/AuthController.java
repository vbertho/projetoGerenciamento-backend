package com.example.projetoGerenciamento.controller;

import com.example.projetoGerenciamento.dto.AuthResponseDTO;
import com.example.projetoGerenciamento.dto.LoginDTO;
import com.example.projetoGerenciamento.dto.RegisterDTO;
import com.example.projetoGerenciamento.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    // registers a new user and returns a JWT token
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterDTO dto) {
        return ResponseEntity.ok(service.register(dto));
    }

    // authenticates the user and returns a JWT token
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginDTO dto) {
        return ResponseEntity.ok(service.login(dto));
    }
}