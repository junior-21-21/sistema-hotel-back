package com.hotel.sistema_hotelero.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el Huésped (1 Huésped puede tener muchas Reservas)
    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    // Relación con la Habitación (1 Habitación puede tener muchas Reservas)
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private double totalPrice; // Precio calculado al momento de la reserva

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING; // Estado inicial

    // Campo para registrar quién hizo la reserva (un empleado, por ejemplo)
    @ManyToOne
    @JoinColumn(name = "booked_by_user_id")
    private User bookedByUser;
}