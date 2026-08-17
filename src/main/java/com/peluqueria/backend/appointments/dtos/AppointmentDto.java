package com.peluqueria.backend.appointments.dtos;

import com.peluqueria.backend.appointments.entities.Appointment;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentDto(
    UUID id,
    UUID userId,
    UUID customerId,
    UUID workerId,
    UUID serviceItemId,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String estado,
    String clienteNombre,
    String clienteTelefono
) {
    public static AppointmentDto fromEntity(Appointment appointment) {
        if (appointment == null) return null;
        
        UUID userId = appointment.getUser() != null ? appointment.getUser().getId() : null;
        UUID customerId = appointment.getCustomer() != null ? appointment.getCustomer().getId() : null;
        
        String nombre = "";
        String telefono = "";
        
        if (appointment.getUser() != null) {
            String apellidos = appointment.getUser().getApellidos();
            nombre = appointment.getUser().getNombre() + (apellidos != null && !apellidos.isEmpty() ? " " + apellidos : "");
            telefono = appointment.getUser().getTelefono();
        } else if (appointment.getCustomer() != null) {
            nombre = appointment.getCustomer().getNombre();
            telefono = appointment.getCustomer().getTelefono();
        }

        return new AppointmentDto(
            appointment.getId(),
            userId,
            customerId,
            appointment.getWorker().getId(),
            appointment.getServiceItem().getId(),
            appointment.getFecha(),
            appointment.getHoraInicio(),
            appointment.getHoraFin(),
            appointment.getEstado().name(),
            nombre,
            telefono
        );
    }
}
