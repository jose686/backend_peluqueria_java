package com.peluqueria.backend.staff.dtos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftRequestDto(
    @NotNull(message = "La fecha es obligatoria")
    LocalDate fecha,

    @NotNull(message = "La hora de inicio es obligatoria")
    LocalTime horaInicio,

    @NotNull(message = "La hora de fin es obligatoria")
    LocalTime horaFin,

    LocalTime breakStartTime,
    LocalTime breakEndTime
) {}
