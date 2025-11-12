package com.hotel.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "guests")
@Data
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String documentId; // DNI, Pasaporte, etc.

    @Column(unique = true)
    private String email;

    private String phone;
}