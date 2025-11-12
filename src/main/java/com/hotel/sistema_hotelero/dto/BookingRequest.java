package com.hotel.sistema_hotelero.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long guestId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}