package com.hotel.sistema_hotelero.dto;

import com.hotel.sistema_hotelero.model.Role;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class EmployeeCreationRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    // El rol que el Administrador asigna: MANAGER o RECEPTIONIST, etc.
    private Role role;
}