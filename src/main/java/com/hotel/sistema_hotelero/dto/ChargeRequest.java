    package com.hotel.sistema_hotelero.dto;

    import lombok.Data;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Positive;

    @Data
    public class ChargeRequest {
        @NotNull
        private Long serviceId; // ID del servicio del catálogo (Ej: Minibar)

        @Positive
        private int quantity;

    }