package com.peluqueria.backend.staff.dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterWorkerDto(
    @NotBlank(message = "El DNI es obligatorio")
    String dni,

    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    String especialidad,

    @NotBlank(message = "La contraseña es obligatoria")
    String password
) {}
