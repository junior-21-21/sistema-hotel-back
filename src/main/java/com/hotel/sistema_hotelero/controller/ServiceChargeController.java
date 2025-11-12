package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.dto.ChargeRequest;
import com.hotel.sistema_hotelero.model.ExtraService;
import com.hotel.sistema_hotelero.service.ServiceChargeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceChargeController {

    @Autowired
    private ServiceChargeService chargeService;

    @GetMapping("/extra-services")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')") // Ajusta los roles según necesites
    public ResponseEntity<List<ExtraService>> getAllExtraServices() {
        // Asegúrate de que este método exista e interactúe con el repositorio/base de datos.
        List<ExtraService> services = chargeService.getAllServices();
        return ResponseEntity.ok(services);
    }
    // =======================
    // CRUD del Catálogo de Servicios (Solo Admin/Manager)
    // =======================
    @PostMapping("/extra-services")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<ExtraService> createExtraService(@RequestBody ExtraService service) {
        ExtraService newService = chargeService.saveService(service);
        return ResponseEntity.status(201).body(newService);
    }

    @PutMapping("/extra-services/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<ExtraService> updateExtraService(
            @PathVariable Long id,
            @RequestBody ExtraService serviceDetails) {
        try {
            // 1. Asegura que el ID del path se use para la actualización
            serviceDetails.setId(id);

            // 2. Llama al método del servicio para manejar la lógica de actualización
            ExtraService updatedService = chargeService.updateService(serviceDetails);

            return ResponseEntity.ok(updatedService);
        } catch (Exception e) {
            // Manejo de errores, por ejemplo, si el servicio no existe
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/extra-services/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteExtraService(@PathVariable Long id) {
        try {
            // Llama al método del servicio para manejar la eliminación
            chargeService.deleteService(id);

            // Devuelve 204 No Content, que es la respuesta estándar para una eliminación exitosa
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            // Maneja la excepción si, por ejemplo, el servicio está en uso (violación de Foreign Key)
            // o si el ID no existe.
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            // Se puede retornar 409 Conflict si la restricción de clave foránea falla.
            return ResponseEntity.badRequest().build();
        }
    }
    // =======================
    // REGISTRO DE CARGOS (Recepción)
    // =======================

    /**
     * Registra un nuevo cargo a una factura existente.
     * Ruta: /api/invoices/{id}/charges
     */
    @PostMapping("/invoices/{invoiceId}/charges")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'RECEPTIONIST')")
    public ResponseEntity<?> registerChargeToInvoice(
            @PathVariable Long invoiceId,
            @RequestBody ChargeRequest request) {
        try {
            chargeService.registerNewCharge(invoiceId, request);
            return ResponseEntity.ok("Cargo registrado y saldo de factura actualizado.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}