package com.peluqueria.backend.appointments.services;

import com.peluqueria.backend.appointments.dtos.AppointmentDto;
import com.peluqueria.backend.appointments.dtos.AppointmentRequest;
import com.peluqueria.backend.appointments.dtos.AvailableSlotsResponse;
import com.peluqueria.backend.appointments.entities.Appointment;
import com.peluqueria.backend.appointments.entities.AppointmentStatus;
import com.peluqueria.backend.appointments.repositories.AppointmentRepository;
import com.peluqueria.backend.catalog.entities.ServiceItem;
import com.peluqueria.backend.catalog.repositories.ServiceItemRepository;
import com.peluqueria.backend.staff.entities.Break;
import com.peluqueria.backend.staff.entities.Shift;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.ShiftRepository;
import com.peluqueria.backend.staff.repositories.WorkerRepository;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.repositories.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final WorkerRepository workerRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ShiftRepository shiftRepository;
    private final UserAccountRepository userRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  WorkerRepository workerRepository,
                                  ServiceItemRepository serviceItemRepository,
                                  ShiftRepository shiftRepository,
                                  UserAccountRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.workerRepository = workerRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableSlotsResponse getAvailableSlots(UUID workerId, UUID serviceItemId, LocalDate fecha) {
        ServiceItem service = serviceItemRepository.findById(serviceItemId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        int duracion = service.getDuracionMinutos();

        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        List<Shift> shifts = shiftRepository.findByWorkerIdAndFecha(workerId, fecha);

        if (shifts.isEmpty()) {
            return new AvailableSlotsResponse(Collections.emptyList());
        }

        List<Appointment> existingAppointments = appointmentRepository
                .findByWorkerIdAndFechaAndEstadoNot(workerId, fecha, AppointmentStatus.CANCELADA);

        List<TimeBlock> occupied = new ArrayList<>();

        for (Shift shift : shifts) {
            if (shift.getBreaks() != null) {
                for (Break b : shift.getBreaks()) {
                    occupied.add(new TimeBlock(b.getHoraInicio(), b.getHoraFin()));
                }
            }
        }

        for (Appointment apt : existingAppointments) {
            occupied.add(new TimeBlock(apt.getHoraInicio(), apt.getHoraFin()));
        }

        List<LocalTime> availableSlots = new ArrayList<>();

        for (Shift shift : shifts) {
            LocalTime cursor = shift.getHoraInicio();
            LocalTime shiftEnd = shift.getHoraFin();

            while (true) {
                LocalTime serviceEnd = cursor.plusMinutes(duracion);

                if (serviceEnd.isAfter(shiftEnd)) {
                    break;
                }

                boolean collision = false;
                for (TimeBlock block : occupied) {
                    if (cursor.isBefore(block.end) && serviceEnd.isAfter(block.start)) {
                        collision = true;
                        cursor = block.end;
                        break;
                    }
                }

                if (!collision) {
                    availableSlots.add(cursor);
                    cursor = cursor.plusMinutes(duracion);
                }
            }
        }

        availableSlots.sort(Comparator.naturalOrder());

        return new AvailableSlotsResponse(availableSlots);
    }

    @Override
    @Transactional
    public AppointmentDto createAppointment(AppointmentRequest request, String email) {
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Worker worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));

        ServiceItem service = serviceItemRepository.findById(request.serviceItemId())
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));

        LocalTime horaFin = request.horaInicio().plusMinutes(service.getDuracionMinutos());

        AvailableSlotsResponse available = getAvailableSlots(
                request.workerId(), request.serviceItemId(), request.fecha());

        if (!available.horasDisponibles().contains(request.horaInicio())) {
            throw new IllegalArgumentException("El horario seleccionado ya no está disponible");
        }

        Appointment appointment = Appointment.builder()
                .user(user)
                .worker(worker)
                .serviceItem(service)
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(horaFin)
                .estado(AppointmentStatus.PENDIENTE)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByUser(String email) {
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return appointmentRepository.findByUserId(user.getId()).stream()
                .map(AppointmentDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDto getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        return AppointmentDto.fromEntity(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto cancelAppointment(UUID id, String email) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.getRole() != Role.ADMIN && !appointment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta cita");
        }

        appointment.setEstado(AppointmentStatus.CANCELADA);
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
        appointmentRepository.delete(appointment);
    }

    private record TimeBlock(LocalTime start, LocalTime end) {}
}
