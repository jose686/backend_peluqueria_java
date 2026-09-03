package com.peluqueria.backend.appointments.controllers;

import com.peluqueria.backend.appointments.dtos.AppointmentDto;
import com.peluqueria.backend.appointments.dtos.AvailableSlotsResponse;
import com.peluqueria.backend.appointments.dtos.OtpRequest;
import com.peluqueria.backend.appointments.dtos.OtpVerifyRequest;
import com.peluqueria.backend.appointments.dtos.PublicBookRequest;
import com.peluqueria.backend.appointments.dtos.MyAppointmentsRequest;
import com.peluqueria.backend.appointments.services.AppointmentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/public/appointments")
public class AppointmentPublicController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentPublicController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/slots")
    public ResponseEntity<AvailableSlotsResponse> getAvailableSlots(
            @RequestParam UUID workerId,
            @RequestParam Long serviceItemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        AvailableSlotsResponse response = appointmentService.getAvailableSlots(workerId, serviceItemId, fecha);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slots/range")
    public ResponseEntity<java.util.Map<LocalDate, Boolean>> getAvailableDaysRange(
            @RequestParam UUID workerId,
            @RequestParam Long serviceItemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        java.util.Map<LocalDate, Boolean> response = appointmentService.getAvailableDaysRange(workerId, serviceItemId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpRequest request) {
        try {
            appointmentService.sendOtp(request.telefono());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Código PIN/OTP enviado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        try {
            boolean valid = appointmentService.verifyOtp(request.telefono(), request.pin());
            Map<String, Object> response = new HashMap<>();
            response.put("valid", valid);
            response.put("message", "Código PIN/OTP verificado correctamente");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookPublic(@Valid @RequestBody PublicBookRequest request) {
        try {
            AppointmentDto dto = appointmentService.createPublicAppointment(request);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/my-appointments")
    public ResponseEntity<?> getMyAppointments(@Valid @RequestBody MyAppointmentsRequest request) {
        try {
            List<AppointmentDto> dtos = appointmentService.getAppointmentsByCustomerPhone(request.telefono(), request.pin());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelPublicAppointment(
            @PathVariable UUID id,
            @Valid @RequestBody OtpVerifyRequest request) {
        try {
            AppointmentDto dto = appointmentService.cancelPublicAppointment(id, request.telefono(), request.pin());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
