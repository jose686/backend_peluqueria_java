package com.peluqueria.backend.appointments.dtos;

import jakarta.validation.constraints.NotBlank;

public record MyAppointmentsRequest(
    @NotBlank(message = "El teléfono es obligatorio")
    String telefono,

    @NotBlank(message = "El PIN es obligatorio")
    String pin
) {}
