package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.model.Guest;
import com.hotel.sistema_hotelero.repository.GuestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestService {

    @Autowired
    private GuestRepository guestRepository;

    public List<Guest> findAllGuests() {
        return guestRepository.findAll();
    }

    public Guest findGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado con ID: " + id));
    }

    public Guest createGuest(Guest guest) {
        // En un sistema real, añadirías validación para documentId/email
        return guestRepository.save(guest);
    }

    public Guest updateGuest(Long id, Guest guestDetails) {
        Guest guest = findGuestById(id);

        // Actualizar solo los campos permitidos
        guest.setFirstName(guestDetails.getFirstName());
        guest.setLastName(guestDetails.getLastName());
        guest.setEmail(guestDetails.getEmail());
        guest.setPhone(guestDetails.getPhone());

        // No se permite cambiar el DocumentId en la actualización por simplicidad.

        return guestRepository.save(guest);
    }

    public void deleteGuest(Long id) {
        // NOTA: En un sistema real, NO debes eliminar un huésped si tiene reservas asociadas.
        if (!guestRepository.existsById(id)) {
            throw new EntityNotFoundException("Huésped no encontrado con ID: " + id);
        }
        guestRepository.deleteById(id);
    }
}