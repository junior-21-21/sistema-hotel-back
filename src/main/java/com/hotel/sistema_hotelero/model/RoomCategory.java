    package com.hotel.sistema_hotelero.model;

    import jakarta.persistence.*;
    import lombok.Data;

    @Entity
    @Table(name = "room_categories")
    @Data
    public class RoomCategory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String name; // Ej: Suite, Doble, Simple

        @Column(nullable = false)
        private double basePrice; // Precio por noche

        private String description;
    }