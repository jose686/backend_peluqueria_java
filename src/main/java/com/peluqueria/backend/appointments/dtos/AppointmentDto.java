package com.peluqueria.backend.appointments.dtos;

import com.peluqueria.backend.appointments.entities.Appointment;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentDto(
    UUID id,
    UUID userId,
    UUID workerId,
    UUID serviceItemId,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    String estado
) {
    public static AppointmentDto fromEntity(Appointment appointment) {
        if (appointment == null) return null;
        return new AppointmentDto(
            appointment.getId(),
            appointment.getUser().getId(),
            appointment.getWorker().getId(),
            appointment.getServiceItem().getId(),
            appointment.getFecha(),
            appointment.getHoraInicio(),
            appointment.getHoraFin(),
            appointment.getEstado().name()
        );
    }
}
