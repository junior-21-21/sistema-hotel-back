package com.hotel.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.Data; // Si usas Lombok

@Entity
@Table(name = "users")
@Data // Genera getters, setters, etc.
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash; // ¡Guardarás el HASH, nunca el texto plano!

    @Enumerated(EnumType.STRING)
    private Role role;

}