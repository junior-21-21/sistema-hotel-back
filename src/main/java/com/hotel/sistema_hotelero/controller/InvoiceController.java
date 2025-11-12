    package com.hotel.sistema_hotelero.controller;

    import com.hotel.sistema_hotelero.dto.ChargeRequest;
    import com.hotel.sistema_hotelero.dto.PaymentRequest;
    import com.hotel.sistema_hotelero.model.Invoice;
    import com.hotel.sistema_hotelero.model.Payment;
    import com.hotel.sistema_hotelero.service.InvoiceService;
    import jakarta.persistence.EntityNotFoundException;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/api/invoices")
    public class InvoiceController {

        @Autowired
        private InvoiceService invoiceService;

        // 1. Ver todas las Facturas
        @GetMapping
        @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
        public ResponseEntity<List<Invoice>> getAllInvoices() {
            return ResponseEntity.ok(invoiceService.findAllInvoices());
        }

        // 1.5. Ver todas las Facturas Activas
        @GetMapping("/active")
        @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
        public ResponseEntity<List<Invoice>> getAllActiveInvoices() {
            // Usa el método que requiere la declaración en InvoiceRepository
            return ResponseEntity.ok(invoiceService.findActiveInvoices());
        }

        // 2. Ver Factura por ID de Factura
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
        public ResponseEntity<?> getInvoiceById(@PathVariable Long id) {
            try {
                return ResponseEntity.ok(invoiceService.findInvoiceById(id));
            } catch (EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }

        // 3. Ver Factura por ID de Reserva (Booking ID)
        @GetMapping("/booking/{bookingId}")
        @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
        public ResponseEntity<?> getInvoiceByBookingId(@PathVariable Long bookingId) {
            try {
                Invoice invoice = invoiceService.findInvoiceByBookingId(bookingId);
                return ResponseEntity.ok(invoice);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            }
        }

        // 4. Registrar un Pago para una Factura
        @PostMapping("/{id}/payments")
        @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
        public ResponseEntity<?> registerPayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
            try {
                Payment newPayment = invoiceService.registerPayment(id, request);
                return ResponseEntity.status(201).body(newPayment);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        // 5. Registrar un Cargo Extra a una Factura
        @PostMapping("/{invoiceId}/service-charges")
        @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
        public ResponseEntity<?> registerCharge(@PathVariable Long invoiceId, @RequestBody ChargeRequest request) {
            try {
                // El servicio (registerChargeToInvoice) devuelve la factura actualizada
                // con el nuevo TotalDue, que es lo que el front-end necesita para refrescar la vista.
                Invoice updatedInvoice = invoiceService.registerChargeToInvoice(invoiceId, request);
                return ResponseEntity.ok(updatedInvoice);
            } catch (EntityNotFoundException e) {
                return ResponseEntity.notFound().build();
            } catch (IllegalArgumentException | IllegalStateException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }
