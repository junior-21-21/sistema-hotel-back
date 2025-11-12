package com.hotel.sistema_hotelero.controller;

import com.hotel.sistema_hotelero.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')") // Restricción a nivel de controlador
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    /**
     * GET /api/reports/occupancy?date=YYYY-MM-DD
     * Muestra la tasa de ocupación (ej: 0.85 para 85%)
     */
    @GetMapping("/occupancy")
    public ResponseEntity<Map<String, Object>> getOccupancyRate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        double rate = reportingService.calculateOccupancyRate(date);

        return ResponseEntity.ok(Map.of(
                "date", date.toString(),
                "occupancyRate", rate,
                "percentage", String.format("%.2f%%", rate * 100)
        ));
    }

    /**
     * GET /api/reports/revenue?start=YYYY-MM-DD&end=YYYY-MM-DD
     */
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        double revenue = reportingService.calculateRevenue(start, end);

        return ResponseEntity.ok(Map.of(
                "startDate", start.toString(),
                "endDate", end.toString(),
                "totalRevenue", revenue
        ));
    }
}