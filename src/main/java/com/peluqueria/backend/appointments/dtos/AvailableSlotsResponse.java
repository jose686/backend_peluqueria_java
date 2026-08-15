package com.peluqueria.backend.appointments.dtos;

import java.time.LocalTime;
import java.util.List;

public record AvailableSlotsResponse(
    List<LocalTime> horasDisponibles
) {}
