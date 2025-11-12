package com.hotel.sistema_hotelero.dto;

import lombok.Data;

@Data // O usa getters/setters si no usas Lombok
public class RegistrationRequest {
    private String username;
    private String password;
}