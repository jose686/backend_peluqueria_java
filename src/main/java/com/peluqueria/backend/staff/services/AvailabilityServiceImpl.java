package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.appointments.dtos.AppointmentDto;
import com.peluqueria.backend.appointments.entities.Appointment;
import com.peluqueria.backend.appointments.entities.AppointmentStatus;
import com.peluqueria.backend.appointments.repositories.AppointmentRepository;
import com.peluqueria.backend.staff.dtos.AvailabilityBlockDto;
import com.peluqueria.backend.staff.entities.Break;
import com.peluqueria.backend.staff.entities.Shift;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.ShiftRepository;
import com.peluqueria.backend.staff.repositories.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityServiceImpl implements AvailabilityService {

    private final WorkerRepository workerRepository;
    private final ShiftRepository shiftRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public AvailabilityServiceImpl(WorkerRepository workerRepository,
                                   ShiftRepository shiftRepository,
                                   AppointmentRepository appointmentRepository) {
        this.workerRepository = workerRepository;
        this.shiftRepository = shiftRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityBlockDto> getAvailabilityGrid(UUID employeeId, LocalDate date) {
        // Buscar el trabajador por UserAccount ID, o por ID de trabajador directo
        Worker worker = workerRepository.findByUserAccountId(employeeId)
                .orElseGet(() -> workerRepository.findById(employeeId)
                        .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado con ID: " + employeeId)));

        List<Shift> shifts = shiftRepository.findByWorkerIdAndFecha(worker.getId(), date);
        List<Appointment> appointments = appointmentRepository
                .findByWorkerIdAndFechaAndEstadoNot(worker.getId(), date, AppointmentStatus.CANCELADA);

        List<AvailabilityBlockDto> grid = new ArrayList<>();

        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(20, 0);
        LocalTime cursor = start;

        while (cursor.isBefore(end)) {
            LocalTime blockStart = cursor;
            LocalTime blockEnd = cursor.plusMinutes(30);

            // 1. Buscar si hay una cita que se solapa con este bloque
            Appointment matchingAppointment = null;
            for (Appointment app : appointments) {
                if (app.getHoraInicio().isBefore(blockEnd) && app.getHoraFin().isAfter(blockStart)) {
                    matchingAppointment = app;
                    break;
                }
            }

            // 2. Comprobar si el bloque cae dentro de la jornada laboral oficial
            boolean withinShift = false;
            boolean isBreak = false;

            for (Shift shift : shifts) {
                if ((shift.getHoraInicio().isBefore(blockStart) || shift.getHoraInicio().equals(blockStart)) &&
                    (shift.getHoraFin().isAfter(blockEnd) || shift.getHoraFin().equals(blockEnd))) {
                    withinShift = true;

                    // Comprobar descansos
                    if (shift.getBreakStartTime() != null && shift.getBreakEndTime() != null) {
                        if (shift.getBreakStartTime().isBefore(blockEnd) && shift.getBreakEndTime().isAfter(blockStart)) {
                            isBreak = true;
                        }
                    }
                    if (shift.getBreaks() != null) {
                        for (Break b : shift.getBreaks()) {
                            if (b.getHoraInicio().isBefore(blockEnd) && b.getHoraFin().isAfter(blockStart)) {
                                isBreak = true;
                            }
                        }
                    }
                }
            }

            // Un bloque es disponible si cae estrictamente dentro de la jornada,
            // no es un descanso, y no tiene cita asignada.
            boolean disponible = withinShift && !isBreak && (matchingAppointment == null);

            grid.add(new AvailabilityBlockDto(
                    blockStart,
                    blockEnd,
                    disponible,
                    matchingAppointment != null ? AvailabilityBlockDto.AppointmentAdminDto.fromEntity(matchingAppointment) : null
            ));

            cursor = blockEnd;
        }

        return grid;
    }
}
