package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.dto.EmployeeCreationRequest;
import com.hotel.sistema_hotelero.dto.RegistrationRequest;
import com.hotel.sistema_hotelero.model.Role;
import com.hotel.sistema_hotelero.model.User;
import com.hotel.sistema_hotelero.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerFirstAdmin(RegistrationRequest request) {
        // 1. VERIFICACIÓN CRÍTICA: Contar usuarios existentes
        if (userRepository.count() > 0) {
            // Si ya existe un usuario (el ADMIN), se prohíbe el registro público.
            // Los nuevos usuarios (empleados) deben ser creados por un ADMIN dentro del sistema.
            throw new IllegalStateException("El sistema ya tiene un administrador. El registro público está deshabilitado.");
        }

        // 2. CREACIÓN DEL ADMINISTRADOR
        User admin = new User();
        admin.setUsername(request.getUsername());

        // Cifrar la contraseña ANTES de guardar (¡Seguridad!)
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Asignación FORZADA del rol de ADMIN
        admin.setRole(Role.ADMIN);

        return userRepository.save(admin);
    }

    public User createEmployee(EmployeeCreationRequest request) {
        // 1. RESTRICCIÓN DE ROL: Nadie puede crear otro ADMIN (solo el primer registro lo permite)
        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("No se permite la creación manual del rol ADMIN.");
        }

        // 2. VERIFICACIÓN DE EXISTENCIA
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new IllegalStateException("El nombre de usuario ya existe.");
        }

        // 3. CREACIÓN DEL EMPLEADO
        User employee = new User();
        employee.setUsername(request.getUsername());

        // Cifrar la contraseña
        employee.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Asignar el rol enviado (MANAGER, RECEPTIONIST, etc.)
        employee.setRole(request.getRole());

        return userRepository.save(employee);
    }
}