package com.hotel.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación N:1 con la Factura (una factura puede tener múltiples pagos)
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private LocalDateTime paymentDate = LocalDateTime.now();

    // Opcional: Registrar quién procesó el pago
    @ManyToOne
    @JoinColumn(name = "processed_by_user_id")
    private User processedByUser;
}