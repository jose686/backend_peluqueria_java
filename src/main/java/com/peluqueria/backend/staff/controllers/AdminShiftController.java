package com.peluqueria.backend.staff.controllers;

import com.peluqueria.backend.staff.dtos.ShiftDto;
import com.peluqueria.backend.staff.dtos.ShiftRequestDto;
import com.peluqueria.backend.staff.services.ShiftService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/shifts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminShiftController {

    private final ShiftService shiftService;

    @Autowired
    public AdminShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping("/worker/{workerId}")
    public ResponseEntity<?> saveShift(
            @PathVariable UUID workerId,
            @Valid @RequestBody ShiftRequestDto request) {
        try {
            ShiftDto created = shiftService.saveShift(workerId, request);
            return ResponseEntity
                    .created(URI.create("/api/v1/admin/shifts/worker/" + workerId))
                    .body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getShiftsByWorker(@PathVariable UUID workerId) {
        try {
            List<ShiftDto> shifts = shiftService.getShiftsByWorker(workerId);
            return ResponseEntity.ok(shifts);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/week")
    public ResponseEntity<?> getShiftsByWeek(@RequestParam String startDate) {
        try {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            List<ShiftDto> shifts = shiftService.getShiftsByWeek(start);
            return ResponseEntity.ok(shifts);
        } catch (Exception ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable UUID id) {
        try {
            shiftService.deleteShift(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Turno eliminado con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
