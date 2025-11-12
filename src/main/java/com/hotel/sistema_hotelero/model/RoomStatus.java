package com.hotel.sistema_hotelero.model;

public enum RoomStatus {
    AVAILABLE,      // Lista para ser ocupada
    OCCUPIED,       // Actualmente con un huésped
    MAINTENANCE,    // En reparación o limpieza profunda
    RESERVED        // Bloqueada por una reserva futura
}