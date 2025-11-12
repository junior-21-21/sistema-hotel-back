package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    // Útil para verificar si un huésped ya existe por su documento
    Guest findByDocumentId(String documentId);
}