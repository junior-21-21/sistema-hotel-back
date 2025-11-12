package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.Booking;
import com.hotel.sistema_hotelero.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Consulta crítica para verificar la disponibilidad de una habitación.
     * Busca cualquier reserva CONFIRMED o CHECKED_IN que se solape con el rango de fechas solicitado.
     * * El solapamiento ocurre si:
     * (A.start <= B.end) AND (A.end >= B.start)
     * A = Rango existente, B = Rango solicitado
     */
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status IN :activeStatuses " +
            "AND b.checkInDate < :newCheckOutDate " +
            "AND b.checkOutDate > :newCheckInDate")
    List<Booking> findConflictingBookings(
            @Param("roomId") Long roomId,
            @Param("newCheckInDate") LocalDate newCheckInDate,
            @Param("newCheckOutDate") LocalDate newCheckOutDate,
            @Param("activeStatuses") List<BookingStatus> activeStatuses
    );
}