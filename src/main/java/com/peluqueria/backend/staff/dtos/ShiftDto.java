package com.peluqueria.backend.staff.dtos;

import com.peluqueria.backend.staff.entities.Shift;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftDto(
    UUID id,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    LocalTime breakStartTime,
    LocalTime breakEndTime,
    UUID workerId
) {
    public static ShiftDto fromEntity(Shift shift) {
        return new ShiftDto(
            shift.getId(),
            shift.getFecha(),
            shift.getHoraInicio(),
            shift.getHoraFin(),
            shift.getBreakStartTime(),
            shift.getBreakEndTime(),
            shift.getWorker() != null ? shift.getWorker().getId() : null
        );
    }
}
