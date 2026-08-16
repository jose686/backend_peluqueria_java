package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.staff.dtos.ShiftDto;
import com.peluqueria.backend.staff.dtos.ShiftRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShiftService {
    ShiftDto saveShift(UUID workerId, ShiftRequestDto request);

    List<ShiftDto> getShiftsByWorker(UUID workerId);

    List<ShiftDto> getShiftsByWeek(LocalDate start);

    void deleteShift(UUID id);

    void copyWeek(LocalDate fromStart, LocalDate toStart);

    void copyWorkerShifts(UUID fromWorkerId, UUID toWorkerId, LocalDate startDate, LocalDate endDate);
}
