package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.model.Guest;
import com.hotel.sistema_hotelero.service.GuestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private GuestService guestService;

    // Obtener y Buscar: Necesario para hacer reservas
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<List<Guest>> getAllGuests() {
        return ResponseEntity.ok(guestService.findAllGuests());
    }

    // Crear: Necesario al hacer una nueva reserva
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<Guest> createGuest(@RequestBody Guest guest) {
        Guest newGuest = guestService.createGuest(guest);
        return ResponseEntity.status(201).body(newGuest);
    }

    // Actualizar: Permitido al personal para corregir datos
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<?> updateGuest(@PathVariable Long id, @RequestBody Guest guestDetails) {
        try {
            Guest updatedGuest = guestService.updateGuest(id, guestDetails);
            return ResponseEntity.ok(updatedGuest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar: Acción restrictiva (solo ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long id) {
        try {
            guestService.deleteGuest(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}