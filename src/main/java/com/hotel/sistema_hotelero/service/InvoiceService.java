package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.dto.ChargeRequest;
import com.hotel.sistema_hotelero.dto.PaymentRequest;
import com.hotel.sistema_hotelero.model.*;
import com.hotel.sistema_hotelero.repository.BookingRepository;
import com.hotel.sistema_hotelero.repository.ExtraServiceRepository; // 💥 Importado
import com.hotel.sistema_hotelero.repository.InvoiceRepository;
import com.hotel.sistema_hotelero.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class InvoiceService {

    private final ExtraServiceRepository extraServiceRepository; // 💥 Campo añadido
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, PaymentRepository paymentRepository,
                          BookingRepository bookingRepository, UserService userService,
                          ExtraServiceRepository extraServiceRepository) { // 💥 Repositorio extra inyectado
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.extraServiceRepository = extraServiceRepository; // 💥 Inicialización
    }

    // ===============================================
    // LÓGICA DE GESTIÓN DEL CICLO DE VIDA
    // ===============================================

    /**
     * Genera la factura inicial al hacer el Check-In.
     */
    @Transactional
    public Invoice createInvoiceForBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada para generar factura."));

        Optional<Invoice> existingInvoiceOptional = invoiceRepository.findByBooking_Id(bookingId);
        if (existingInvoiceOptional.isPresent()) {
            return existingInvoiceOptional.get();
        }

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setTotalDue(booking.getTotalPrice());
        invoice.setTotalPaid(0.0);
        invoice.setStatus(InvoiceStatus.PENDING);

        return invoiceRepository.save(invoice);
    }

    /**
     * Procesa un pago parcial o total.
     */
    @Transactional
    public Payment registerPayment(Long invoiceId, PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada."));

        double remainingDue = invoice.getTotalDue() - invoice.getTotalPaid();

        if (remainingDue <= 0) {
            throw new IllegalStateException("La factura ya está totalmente pagada.");
        }
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser positivo.");
        }

        double amountToPay = Math.min(request.getAmount(), remainingDue);

        // Crear y guardar el registro de Payment
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(amountToPay);
        payment.setMethod(request.getMethod());

        // Asignar el usuario que procesa el pago
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userService.findByUsername(currentUsername);
        currentUser.ifPresent(payment::setProcessedByUser);

        Payment savedPayment = paymentRepository.save(payment);

        // Actualizar el saldo de la Factura
        invoice.setTotalPaid(invoice.getTotalPaid() + amountToPay);

        // Cambiar el estado a PAGADA si el saldo es cero
        if (Math.abs(invoice.getTotalDue() - invoice.getTotalPaid()) < 0.01) {
            invoice.setStatus(InvoiceStatus.PAID);
        }
        invoiceRepository.save(invoice);

        return savedPayment;
    }

    /**
     * Anula la factura al cancelarse la reserva (deuda a cero).
     */
    @Transactional
    public void cancelInvoiceForBooking(Long bookingId) {
        Invoice invoice = invoiceRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada para la reserva ID: " + bookingId));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new IllegalStateException("No se puede anular una factura que ya ha sido pagada.");
        }

        invoice.setTotalDue(0.00);
        invoice.setStatus(InvoiceStatus.VOIDED);

        invoiceRepository.save(invoice);
    }

    /**
     * Cierra la factura y procesa el pago final durante el Check-Out.
     */
    @Transactional
    public void closeInvoiceAndProcessPayment(Long bookingId) {
        Invoice invoice = invoiceRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada para la reserva ID: " + bookingId));

        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            return;
        }

        double outstandingBalance = invoice.getTotalDue() - invoice.getTotalPaid();

        if (outstandingBalance > 0) {
            // SIMULACIÓN: Asumimos pago exitoso del saldo pendiente
            invoice.setTotalPaid(invoice.getTotalDue());
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
    }

    /**
     * Registra un cargo extra y actualiza el total de la factura.
     */
    @Transactional
    public Invoice registerChargeToInvoice(Long invoiceId, ChargeRequest request) {
        // 1. Encontrar la Factura
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada con ID: " + invoiceId));

        // 2. Encontrar el Servicio Extra para obtener el precio
        ExtraService extraService = extraServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Servicio extra no encontrado con ID: " + request.getServiceId()));

        // 3. Calcular el monto del cargo
        double chargeAmount = request.getQuantity() * extraService.getPrice();

        // 4. Actualizar el saldo de la Factura
        invoice.setTotalDue(invoice.getTotalDue() + chargeAmount);

        // Nota: Considerar aquí crear y guardar una entidad ServiceCharge si la usas para el detalle.

        // 5. Devolver la factura actualizada
        return invoiceRepository.save(invoice);
    }


    // ===============================================
    // CONSULTAS
    // ===============================================
    public List<Invoice> findActiveInvoices() {
        return invoiceRepository.findByStatus(InvoiceStatus.PENDING);
    }
    public List<Invoice> findAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice findInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada con ID: " + id));
    }

    public Invoice findInvoiceByBookingId(Long bookingId) {
        return invoiceRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada para la reserva ID: " + bookingId));
    }

    /**
     * Devuelve todos los pagos registrados para una factura específica.
     * 💥 CORREGIDO: Uso de findByInvoice_Id, asumiendo la convención JPA.
     */
    public List<Payment> findAllPaymentsForInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }
}