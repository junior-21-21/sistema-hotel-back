package com.hotel.sistema_hotelero.model;

public enum BookingStatus {
    PENDING,        // Solicitud hecha, esperando confirmación (pago).
    CONFIRMED,      // Reserva activa y garantizada.
    CHECKED_IN,     // Huésped ha llegado y está ocupando la habitación.
    CHECKED_OUT,    // Huésped ha salido (reserva completada).
    CANCELLED,      // Reserva anulada por el huésped o el hotel.
    NO_SHOW         // El huésped nunca se presentó.
}