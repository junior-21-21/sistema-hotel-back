package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // En com.hotel.sistema_hotelero.repository.PaymentRepository
    List<Payment> findByInvoiceId(Long invoiceId);
}