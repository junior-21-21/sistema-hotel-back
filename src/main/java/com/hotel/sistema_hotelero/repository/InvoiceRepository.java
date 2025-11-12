package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.Invoice;
import com.hotel.sistema_hotelero.model.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// En com.hotel.sistema_hotelero.repository.InvoiceRepository.java
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Spring infiere el campo 'booking' dentro de Invoice.
    // Necesitas acceder al ID del Booking dentro de esa relación.
    Optional<Invoice> findByBooking_Id(Long bookingId); // ⬅️ DEBERÍA SER ESTA FIRMA
    List<Invoice> findByStatus(InvoiceStatus status);
}