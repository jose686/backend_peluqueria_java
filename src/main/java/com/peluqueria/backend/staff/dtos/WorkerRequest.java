package com.peluqueria.backend.staff.dtos;

import jakarta.validation.constraints.NotBlank;

public record WorkerRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    String especialidad
) {}
