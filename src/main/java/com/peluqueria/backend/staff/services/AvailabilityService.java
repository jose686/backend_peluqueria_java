package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.staff.dtos.AvailabilityBlockDto;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {
    List<AvailabilityBlockDto> getAvailabilityGrid(UUID employeeId, LocalDate date);
}
