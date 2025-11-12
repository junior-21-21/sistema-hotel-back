package com.hotel.sistema_hotelero.service;

import com.hotel.sistema_hotelero.dto.ChargeRequest;
import com.hotel.sistema_hotelero.model.*;
import com.hotel.sistema_hotelero.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceChargeService {
    @Autowired
    private ExtraServiceRepository extraServiceRepository;
    @Autowired private ServiceChargeRepository chargeRepository;
    @Autowired private ExtraServiceRepository serviceRepository;
    @Autowired private InvoiceRepository invoiceRepository;

    /**
     * Registra un cargo por servicio y actualiza el saldo total de la factura.
     */
    @Transactional
    public ServiceCharge registerNewCharge(Long invoiceId, ChargeRequest request) {

        // 1. Obtener Factura, Servicio y Calcular Monto
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada con ID: " + invoiceId));

        ExtraService extraService = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Servicio extra no encontrado con ID: " + request.getServiceId()));

        double totalCharge = request.getQuantity() * extraService.getPrice();

        // 2. Crear y Guardar el Registro de Cargo
        ServiceCharge charge = new ServiceCharge();
        charge.setInvoice(invoice);
        charge.setService(extraService);
        charge.setQuantity(request.getQuantity());
        charge.setChargeAmount(totalCharge);

        ServiceCharge savedCharge = chargeRepository.save(charge);

        // 3. ACTUALIZAR EL SALDO DE LA FACTURA (¡CRUCIAL!)
        invoice.setTotalDue(invoice.getTotalDue() + totalCharge);

        // Si la factura estaba PAGADA y se añade un cargo, debe volver a PENDING
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            invoice.setStatus(InvoiceStatus.PENDING);
        }
        invoiceRepository.save(invoice);

        return savedCharge;
    }

    // Método para crear/actualizar el catálogo (CRUD simple)
    public ExtraService saveService(ExtraService service) {
        return serviceRepository.save(service);
    }

    // ... (Métodos para buscar el catálogo, eliminar, etc.)
    public List<ExtraService> getAllServices() {
        // Usa el método findAll() de JpaRepository para obtener todos los registros.
        return extraServiceRepository.findAll();
    }
    public ExtraService updateService(ExtraService service) {
        // Si el 'id' del objeto 'service' está presente,
        // JpaRepository.save() lo detecta como una actualización (PUT).
        return extraServiceRepository.save(service);
    }
    public void deleteService(Long id) {
        // Usa el método deleteById() del JpaRepository
        extraServiceRepository.deleteById(id);
    }
}