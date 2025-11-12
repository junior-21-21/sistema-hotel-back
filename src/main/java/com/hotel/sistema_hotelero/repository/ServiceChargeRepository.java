package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.ServiceCharge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceChargeRepository extends JpaRepository<ServiceCharge, Long> {
}