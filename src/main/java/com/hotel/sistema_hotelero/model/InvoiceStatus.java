package com.hotel.sistema_hotelero.model;

public enum InvoiceStatus {
    PENDING,  // Factura creada, saldo pendiente.
    PAID,     // Pagada en su totalidad.
    OVERDUE,  // Vencida (si aplica).
    VOIDED    // Anulada.
}