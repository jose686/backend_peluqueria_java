package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.staff.dtos.ShiftDto;
import com.peluqueria.backend.staff.dtos.ShiftRequestDto;

import java.util.List;
import java.util.UUID;

public interface ShiftService {
    ShiftDto saveShift(UUID workerId, ShiftRequestDto request);

    List<ShiftDto> getShiftsByWorker(UUID workerId);
}
