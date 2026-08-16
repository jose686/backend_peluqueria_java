package com.peluqueria.backend.staff.controllers;

import com.peluqueria.backend.staff.dtos.AvailabilityBlockDto;
import com.peluqueria.backend.staff.services.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public ResponseEntity<?> getAvailabilityGrid(
            @RequestParam UUID employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate != null && endDate != null) {
            Map<LocalDate, List<AvailabilityBlockDto>> rangeGrid = availabilityService.getAvailabilityGridRange(employeeId, startDate, endDate);
            return ResponseEntity.ok(rangeGrid);
        }

        if (date == null) {
            date = LocalDate.now();
        }

        return ResponseEntity.ok(availabilityService.getAvailabilityGrid(employeeId, date));
    }
}
