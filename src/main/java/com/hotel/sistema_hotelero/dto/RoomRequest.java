    package com.hotel.sistema_hotelero.dto;

    import com.hotel.sistema_hotelero.model.RoomStatus;
    import lombok.Data;

    @Data
    public class RoomRequest {
        private String roomNumber;
        private Long categoryId; // Usamos el ID de la categoría
        private RoomStatus status;
    }