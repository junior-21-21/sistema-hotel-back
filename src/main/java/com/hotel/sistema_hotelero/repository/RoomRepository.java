package com.hotel.sistema_hotelero.repository;

import com.hotel.sistema_hotelero.model.Room;
import com.hotel.sistema_hotelero.model.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(RoomStatus status);
}