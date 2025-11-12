package com.hotel.sistema_hotelero.repository;


import com.hotel.sistema_hotelero.model.RoomCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomCategoryRepository extends JpaRepository<RoomCategory, Long> {
}