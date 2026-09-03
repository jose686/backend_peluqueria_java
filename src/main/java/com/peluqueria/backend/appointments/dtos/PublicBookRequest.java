package com.peluqueria.backend.appointments.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record PublicBookRequest(
    @NotNull(message = "El trabajador es obligatorio")
    UUID workerId,

    @NotNull(message = "El servicio es obligatorio")
    Long serviceItemId,

    @NotNull(message = "La fecha es obligatoria")
    LocalDate fecha,

    @NotNull(message = "La hora de inicio es obligatoria")
    LocalTime horaInicio,

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotBlank(message = "El teléfono es obligatorio")
    String telefono,

    @NotBlank(message = "El PIN de verificación es obligatorio")
    String pin
) {}
