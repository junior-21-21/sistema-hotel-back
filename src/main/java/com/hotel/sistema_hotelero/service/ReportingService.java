package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.model.BookingStatus;
import com.hotel.sistema_hotelero.model.Invoice;
import com.hotel.sistema_hotelero.model.InvoiceStatus;
import com.hotel.sistema_hotelero.model.Payment;
import com.hotel.sistema_hotelero.repository.BookingRepository;
import com.hotel.sistema_hotelero.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private InvoiceService invoiceService;

    // Estados de reserva que cuentan como ocupadas
    private static final List<BookingStatus> OCCUPIED_STATUSES =
            List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN);

    // ===============================================
    // REPORTE 1: TASA DE OCUPACIÓN
    // ===============================================

    /**
     * Calcula la tasa de ocupación para una fecha específica.
     * Tasa = (Habitaciones Ocupadas) / (Total de Habitaciones)
     */
    public double calculateOccupancyRate(LocalDate date) {
        // 1. Contar el total de habitaciones
        long totalRooms = roomRepository.count();
        if (totalRooms == 0) return 0.0;

        // 2. Contar reservas activas (ocupadas o confirmadas para ese día)
        // Usamos la misma lógica de solapamiento que en BookingService, pero buscando reservas que
        // incluyan solo el día específico, ajustando el checkOutDate a > date.

        long occupiedRoomsCount = bookingRepository.findAll().stream()
                .filter(b -> OCCUPIED_STATUSES.contains(b.getStatus()))
                .filter(b -> !date.isBefore(b.getCheckInDate()) && date.isBefore(b.getCheckOutDate()))
                .count();

        // Nota: Una consulta JPA nativa o JPQL sería más eficiente aquí, pero por claridad, usamos Stream.
        // Consulta eficiente en BookingRepository (solo un ejemplo de cómo se haría):
        /*
        @Query("SELECT COUNT(DISTINCT b.room.id) FROM Booking b WHERE " +
           "b.status IN :activeStatuses AND :date BETWEEN b.checkInDate AND b.checkOutDate")
        Long countOccupiedRooms(@Param("date") LocalDate date, @Param("activeStatuses") List<BookingStatus> activeStatuses);
        */

        return (double) occupiedRoomsCount / totalRooms;
    }

    // ===============================================
    // REPORTE 2: INGRESOS POR PERÍODO
    // ===============================================

    /**
     * Calcula los ingresos totales (pagos registrados) entre dos fechas.
     */
    public double calculateRevenue(LocalDate startDate, LocalDate endDate) {

        // 1. Obtener todas las facturas que están PAGADAS.
        // Esto simplifica el reporte a ingresos REALES.
        List<Invoice> paidInvoices = invoiceService.findAllInvoices().stream()
                .filter(i -> i.getStatus() == InvoiceStatus.PAID)
                .collect(Collectors.toList());

        // 2. Filtrar pagos dentro del rango de fechas y sumarlos
        double totalRevenue = paidInvoices.stream()
                .flatMap(invoice -> invoiceService.findAllPaymentsForInvoice(invoice.getId()).stream()) // Asume que necesitas un findAllPaymentsForInvoice
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .mapToDouble(Payment::getAmount)
                .sum();

        return totalRevenue;
    }
}