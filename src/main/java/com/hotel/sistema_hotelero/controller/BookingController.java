package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.dto.BookingRequest;
import com.hotel.sistema_hotelero.model.Booking;
import com.hotel.sistema_hotelero.model.BookingStatus;
import com.hotel.sistema_hotelero.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // 1. Ver todas las Reservas
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAllBookings());
    }

    // 2. Crear Reserva
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            Booking newBooking = bookingService.createBooking(request);
            return ResponseEntity.status(201).body(newBooking);
        } catch (EntityNotFoundException | IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Editar Reserva (PUT)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody BookingRequest request) {
        try {
            Booking updatedBooking = bookingService.updateBooking(id, request);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 4. Actualización de Estado Genérica (PATCH /api/bookings/{id}/status)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam BookingStatus newStatus) {
        try {
            Booking updatedBooking = bookingService.updateBookingStatus(id, newStatus);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Check-In Específico (PATCH /api/bookings/{id}/checkin)
    @PatchMapping("/{id}/checkin")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        try {
            Booking updatedBooking = bookingService.checkIn(id);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6. 💥 NUEVO: Check-Out Específico (PATCH /api/bookings/{id}/checkout)
    @PatchMapping("/{id}/checkout")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<?> checkOut(@PathVariable Long id) {
        try {
            // Se asume que BookingService tiene un método checkOut que cambia el estado a CHECKED_OUT
            Booking updatedBooking = bookingService.checkOut(id);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 7. 💥 NUEVO: Marcar como No-Show (PATCH /api/bookings/{id}/noshow)
    @PatchMapping("/{id}/noshow")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<?> noShow(@PathVariable Long id) {
        try {
            // Se asume que BookingService tiene un método noShow que cambia el estado a NO_SHOW
            Booking updatedBooking = bookingService.noShow(id);
            return ResponseEntity.ok(updatedBooking);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 8. Cancelar Reserva (PATCH /api/bookings/{id}/cancel)
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            // El servicio llama al método que cambia el estado a CANCELLED
            Booking cancelledBooking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(cancelledBooking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 9. Eliminar Físicamente
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}