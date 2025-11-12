package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.dto.AuthResponse;
import com.hotel.sistema_hotelero.dto.RegistrationRequest;
import com.hotel.sistema_hotelero.security.JwtService;
import com.hotel.sistema_hotelero.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private JwtService jwtService;
    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody RegistrationRequest request) {
        try {
            authService.registerFirstAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Primer Administrador registrado exitosamente.");
        } catch (IllegalStateException e) {
            // Manejo de la excepción cuando el sistema ya tiene un ADMIN
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar registrar.");
        }
    }



    // ... (Método registerAdmin ya implementado) ...

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody RegistrationRequest request) {
        // 1. Autenticación: Spring Security valida credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Cargar detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 3. Generar el Token JWT
        final String jwt = jwtService.generateToken(userDetails);

        // 4. Retornar el token a Angular
        return ResponseEntity.ok(AuthResponse.builder().token(jwt).build());
    }
}