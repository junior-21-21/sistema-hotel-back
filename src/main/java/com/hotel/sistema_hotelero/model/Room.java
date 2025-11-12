    package com.hotel.sistema_hotelero.model;

    import jakarta.persistence.*;
    import lombok.Data;

    @Entity
    @Table(name = "rooms")
    @Data
    public class Room {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String roomNumber; // Ej: "101", "205A"

        @Enumerated(EnumType.STRING)
        private RoomStatus status = RoomStatus.AVAILABLE; // Estado por defecto

        @ManyToOne // Relación N:1 con la categoría
        @JoinColumn(name = "category_id", nullable = false)
        private RoomCategory category;
    }