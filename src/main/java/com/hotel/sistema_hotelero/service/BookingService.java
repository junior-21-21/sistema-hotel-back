package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.dto.BookingRequest;
import com.hotel.sistema_hotelero.model.*;
import com.hotel.sistema_hotelero.repository.BookingRepository;
import com.hotel.sistema_hotelero.repository.GuestRepository;
import com.hotel.sistema_hotelero.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserService userService; // Asumiendo que tienes un UserService
    @Autowired
    private RoomService roomService;
    @Autowired
    private InvoiceService invoiceService;

    // Estados que indican que la habitación NO está libre
    private static final List<BookingStatus> ACTIVE_STATUSES =
            Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN);

    // ===============================================
    // LÓGICA DE DISPONIBILIDAD (EL CORAZÓN)
    // ===============================================
    public Booking checkIn(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada."));

        // 1. Validaciones
        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("La reserva no puede hacer check-in desde el estado actual: " + booking.getStatus());
        }

        // 2. CREACIÓN DE LA FACTURA (¡Nueva integración!)
        // Este paso es crucial. Se asegura de que exista una factura con el total de la reserva
        // antes de que el huésped ocupe la habitación.
        // El InvoiceService gestiona la lógica de no crear duplicados.
        invoiceService.createInvoiceForBooking(bookingId);

        // 3. Actualizar estado de la HABITACIÓN
        // Ahora, la habitación pasa a estar ocupada.
        roomService.updateRoomStatus(booking.getRoom().getId(), RoomStatus.OCCUPIED);

        // 4. Actualizar estado de la RESERVA
        // La reserva cambia a CHECKED_IN.
        booking.setStatus(BookingStatus.CHECKED_IN);

        return bookingRepository.save(booking);
    }

    /**
     * Procesa la salida del huésped.
     * 1. Cambia el estado de la Reserva a CHECKED_OUT.
     * 2. Cambia el estado de la Habitación a AVAILABLE (lista para limpieza/uso).
     */
    // En com.hotel.sistema_hotelero.service.BookingService.java

// ... (inyecciones y demás métodos)

    // En com.hotel.sistema_hotelero.service.BookingService.java

    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada."));

        // 1. Validaciones
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Solo se puede hacer check-out desde el estado CHECKED_IN.");
        }

        // 💥 2. CIERRE DE FACTURA Y COBRO FINAL 💥
        // ESTE PASO ES VITAL: Si el cobro falla, el check-out debe fallar.
        invoiceService.closeInvoiceAndProcessPayment(bookingId);

        // 3. Actualizar estado de la HABITACIÓN a MANTENIMIENTO (Sucia)
        roomService.updateRoomStatus(booking.getRoom().getId(), RoomStatus.MAINTENANCE);

        // 4. Actualizar estado de la RESERVA
        booking.setStatus(BookingStatus.CHECKED_OUT);

        return bookingRepository.save(booking);
    }
    public boolean isRoomAvailable(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        // 1. Validar fechas
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("La fecha de entrada debe ser anterior a la de salida.");
        }

        // 2. Buscar conflictos en la base de datos
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                roomId,
                checkInDate,
                checkOutDate,
                ACTIVE_STATUSES
        );

        // Si la lista de conflictos está vacía, la habitación está disponible.
        return conflicts.isEmpty();
    }

    // ===============================================
    // CRUD Y CREACIÓN DE RESERVA
    // ===============================================

    public Booking createBooking(BookingRequest request) {

        // 1. Validación de Entidades y Fechas
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Habitación no encontrada."));

        Guest guest = guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new EntityNotFoundException("Huésped no encontrado."));

        // 2. Verificación de Disponibilidad (la lógica crítica)
        if (!isRoomAvailable(room.getId(), request.getCheckInDate(), request.getCheckOutDate())) {
            throw new IllegalStateException("La habitación no está disponible para las fechas solicitadas.");
        }

        // 3. Cálculo de Precio
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        if (nights <= 0) {
            throw new IllegalArgumentException("La reserva debe ser de al menos una noche.");
        }
        double total = nights * room.getCategory().getBasePrice();

        // 4. Mapeo y Guardado
        Booking newBooking = new Booking();
        newBooking.setGuest(guest);
        newBooking.setRoom(room);
        newBooking.setCheckInDate(request.getCheckInDate());
        newBooking.setCheckOutDate(request.getCheckOutDate());
        newBooking.setTotalPrice(total);
        newBooking.setStatus(BookingStatus.CONFIRMED); // Asume confirmación inmediata

        // Opcional: Registrar al empleado que realizó la acción
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userService.findByUsername(currentUsername); // Necesitas implementar findByUsername
        currentUser.ifPresent(newBooking::setBookedByUser);

        return bookingRepository.save(newBooking);
    }

    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    // Método para cambiar el estado (Check-in/Check-out)
    public Booking updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada."));

        // Lógica de transición de estados (p. ej., no se puede pasar de CANCELLED a CHECKED_IN)
        booking.setStatus(newStatus);

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long bookingId) {
        // 1. Verificar si la reserva existe
        if (!bookingRepository.existsById(bookingId)) {
            throw new EntityNotFoundException("Reserva no encontrada con ID: " + bookingId);
        }
    }
    // Ejemplo de método requerido en BookingService.java

    public Booking updateBooking(Long id, BookingRequest request) {
        // 1. Buscar la reserva por ID
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + id));

        // 2. Aquí irían las validaciones de negocio (ej. no permitir editar después del check-in)
        if (existingBooking.getStatus() == BookingStatus.CHECKED_IN || existingBooking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException("No se puede modificar una reserva que ya ha sido procesada.");
        }

        // 3. Aplicar los cambios del request al objeto existente
        // (Actualizar guestId, roomId, checkInDate, checkOutDate, etc.)

        // 4. Guardar y devolver
        return bookingRepository.save(existingBooking);
    }

    // Ejemplo de método requerido en BookingService.java

    @Transactional // Asegura que la reserva y la factura se actualicen juntas
    public Booking cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + id));

        // 1. Lógica de validación: No se puede cancelar si ya ha sido procesada.
        if (booking.getStatus() == BookingStatus.CHECKED_OUT || booking.getStatus() == BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("No se puede cancelar una reserva que ya ha finalizado o está en curso.");
        }

        // 💥 2. ANULAR LA FACTURA (CERO DEUDA) 💥
        // Se llama al método en el InvoiceService para:
        // a) Encontrar la Factura asociada al Booking ID.
        // b) Establecer el Total en 0.00 (o el monto de penalización).
        // c) Cambiar el estado de la Factura a VOID/CANCELLED.
        invoiceService.cancelInvoiceForBooking(id);

        // 3. Cambiar el estado de la Reserva
        booking.setStatus(BookingStatus.CANCELLED);

        // 4. Liberar la Habitación
        // Si la habitación estaba bloqueada (CONFIRMED o PENDING), se libera.
        if (booking.getRoom() != null) {
            roomService.updateRoomStatus(booking.getRoom().getId(), RoomStatus.AVAILABLE);
        }

        return bookingRepository.save(booking);
    }


    public Booking noShow(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + id));

        if (booking.getStatus() != BookingStatus.CONFIRMED && booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Solo se puede marcar como NO_SHOW reservas CONFIRMED o PENDING.");
        }

        booking.setStatus(BookingStatus.NO_SHOW);

        // Opcional: Aplicar cargos por no presentarse (No-Show Fee)

        // 💥 Importante: Liberar la habitación
        // roomService.updateRoomStatus(booking.getRoom().getId(), RoomStatus.AVAILABLE);

        return bookingRepository.save(booking);
    }
}