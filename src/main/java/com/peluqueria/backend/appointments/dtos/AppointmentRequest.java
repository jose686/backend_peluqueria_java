package com.peluqueria.backend.appointments.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentRequest(
    @NotNull(message = "El trabajador es obligatorio")
    UUID workerId,

    @NotNull(message = "El servicio es obligatorio")
    Long serviceItemId,

    @NotNull(message = "La fecha es obligatoria")
    LocalDate fecha,

    @NotNull(message = "La hora de inicio es obligatoria")
    LocalTime horaInicio
) {}
