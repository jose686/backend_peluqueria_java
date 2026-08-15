package com.peluqueria.backend.appointments.dtos;

import java.time.LocalTime;

public record TimeSlotDTO(
    LocalTime horaInicio,
    LocalTime horaFin,
    boolean disponible
) {}
