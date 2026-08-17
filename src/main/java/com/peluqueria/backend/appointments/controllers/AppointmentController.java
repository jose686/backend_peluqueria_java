package com.peluqueria.backend.appointments.controllers;

import com.peluqueria.backend.appointments.dtos.AppointmentDto;
import com.peluqueria.backend.appointments.dtos.AppointmentRequest;
import com.peluqueria.backend.appointments.dtos.AvailableSlotsResponse;
import com.peluqueria.backend.appointments.services.AppointmentService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Endpoint para consultar los huecos disponibles de un peluquero en una fecha determinada.
     */
    @GetMapping("/available")
    public ResponseEntity<AvailableSlotsResponse> getAvailableSlots(
            @RequestParam UUID workerId,
            @RequestParam UUID serviceItemId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        AvailableSlotsResponse response = appointmentService.getAvailableSlots(workerId, serviceItemId, fecha);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para crear una nueva reserva de cita.
     */
    @PostMapping
    public ResponseEntity<?> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            AppointmentDto dto = appointmentService.createAppointment(request, email);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para recuperar las citas del usuario autenticado actualmente.
     */
    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getMyAppointments(Authentication authentication) {
        String email = authentication.getName();
        List<AppointmentDto> dtos = appointmentService.getAppointmentsByUser(email);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsForAdmin() {
        List<AppointmentDto> dtos = appointmentService.getAllAppointments();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam com.peluqueria.backend.appointments.entities.AppointmentStatus estado) {
        try {
            AppointmentDto dto = appointmentService.updateAppointmentStatus(id, estado);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para obtener el detalle de una cita por su identificador.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable UUID id) {
        try {
            AppointmentDto dto = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    /**
     * Endpoint para solicitar la cancelación de una cita activa.
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable UUID id, Authentication authentication) {
        try {
            String email = authentication.getName();
            AppointmentDto dto = appointmentService.cancelAppointment(id, email);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Endpoint para eliminar definitivamente una cita (sólo accesible para administradores).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        try {
            appointmentService.deleteAppointment(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cita eliminada con éxito");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
