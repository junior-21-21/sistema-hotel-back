package com.hotel.sistema_hotelero.dto;

import com.hotel.sistema_hotelero.model.PaymentMethod;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class PaymentRequest {
    @NotNull
    @Positive // Asegura que el monto sea positivo
    private double amount;

    @NotNull
    private PaymentMethod method;
}