package com.hotel.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "extra_services")
@Data
public class ExtraService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ej: Minibar, Lavandería, Room Service

    @Column(nullable = false)
    private double price; // Precio unitario del servicio
    @Column(name = "description")
    private String description;
}