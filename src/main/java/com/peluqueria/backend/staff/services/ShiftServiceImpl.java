package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.staff.dtos.ShiftDto;
import com.peluqueria.backend.staff.dtos.ShiftRequestDto;
import com.peluqueria.backend.staff.entities.Shift;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.ShiftRepository;
import com.peluqueria.backend.staff.repositories.WorkerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final WorkerRepository workerRepository;

    @Autowired
    public ShiftServiceImpl(ShiftRepository shiftRepository, WorkerRepository workerRepository) {
        this.shiftRepository = shiftRepository;
        this.workerRepository = workerRepository;
    }

    @Override
    @Transactional
    public ShiftDto saveShift(UUID workerId, ShiftRequestDto request) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));

        // Validaciones del horario de la jornada y descanso
        if (request.horaInicio().isAfter(request.horaFin())) {
            throw new IllegalArgumentException("La hora de inicio de la jornada debe ser previa a la hora de fin.");
        }

        if (request.breakStartTime() != null && request.breakEndTime() != null) {
            if (request.breakStartTime().isBefore(request.horaInicio()) ||
                    request.breakEndTime().isAfter(request.horaFin()) ||
                    request.breakStartTime().isAfter(request.breakEndTime())) {
                throw new IllegalArgumentException(
                        "El descanso debe estar dentro de la jornada laboral y la hora de inicio del descanso debe ser previa a su fin.");
            }
        }

        Shift shift = Shift.builder()
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(request.horaFin())
                .breakStartTime(request.breakStartTime())
                .breakEndTime(request.breakEndTime())
                .worker(worker)
                .build();

        Shift saved = shiftRepository.save(shift);
        return ShiftDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDto> getShiftsByWorker(UUID workerId) {
        if (!workerRepository.existsById(workerId)) {
            throw new IllegalArgumentException("Trabajador no encontrado");
        }
        return shiftRepository.findByWorkerId(workerId).stream()
                .map(ShiftDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftDto> getShiftsByWeek(LocalDate start) {
        LocalDate end = start.plusDays(6);
        return shiftRepository.findByFechaBetween(start, end).stream()
                .map(ShiftDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteShift(UUID id) {
        if (!shiftRepository.existsById(id)) {
            throw new IllegalArgumentException("Turno no encontrado");
        }
        shiftRepository.deleteById(id);
    }
}
