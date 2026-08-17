package com.peluqueria.backend.appointments.dtos;

import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
    @NotBlank(message = "El teléfono es obligatorio")
    String telefono
) {}
