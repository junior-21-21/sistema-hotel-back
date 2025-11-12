package com.hotel.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_charges")
@Data
public class ServiceCharge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación N:1 con la Factura (múltiples cargos en una factura)
    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // Relación N:1 con el Catálogo de Servicios
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ExtraService service;

    @Column(nullable = false)
    private int quantity; // Cantidad de unidades consumidas

    @Column(nullable = false)
    private double chargeAmount; // Monto total del cargo (quantity * service.price)

    private LocalDateTime chargeDate = LocalDateTime.now();
}