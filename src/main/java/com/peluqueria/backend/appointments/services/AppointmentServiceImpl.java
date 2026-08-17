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

import com.peluqueria.backend.appointments.entities.Customer;
import com.peluqueria.backend.appointments.entities.AppointmentOtp;
import com.peluqueria.backend.appointments.repositories.CustomerRepository;
import com.peluqueria.backend.appointments.repositories.AppointmentOtpRepository;
import com.peluqueria.backend.appointments.dtos.PublicBookRequest;
import java.time.LocalDateTime;

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
    private final CustomerRepository customerRepository;
    private final AppointmentOtpRepository otpRepository;
    private final NotificationSenderService notificationSenderService;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  WorkerRepository workerRepository,
                                  ServiceItemRepository serviceItemRepository,
                                  ShiftRepository shiftRepository,
                                  UserAccountRepository userRepository,
                                  CustomerRepository customerRepository,
                                  AppointmentOtpRepository otpRepository,
                                  NotificationSenderService notificationSenderService) {
        this.appointmentRepository = appointmentRepository;
        this.workerRepository = workerRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.otpRepository = otpRepository;
        this.notificationSenderService = notificationSenderService;
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
    @Transactional(readOnly = true)
    public Map<LocalDate, Boolean> getAvailableDaysRange(UUID workerId, UUID serviceItemId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Boolean> result = new HashMap<>();
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            AvailableSlotsResponse slots = getAvailableSlots(workerId, serviceItemId, cursor);
            result.put(cursor, !slots.horasDisponibles().isEmpty());
            cursor = cursor.plusDays(1);
        }
        return result;
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

        if (user.getRole() == com.peluqueria.backend.users.entities.Role.ADMIN) {
            return appointmentRepository.findAll().stream()
                    .map(AppointmentDto::fromEntity)
                    .toList();
        }

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

    private String normalizeTelefono(String telefono) {
        if (telefono == null) return null;
        String clean = telefono.replaceAll("[\\s\\-\\(\\)]", "");
        if (!clean.startsWith("+")) {
            if (clean.length() == 9) {
                return "+34" + clean;
            }
        }
        return clean;
    }

    @Override
    @Transactional
    public void sendOtp(String telefono) {
        String normalized = normalizeTelefono(telefono);
        
        // Invalida los anteriores para el mismo teléfono
        List<AppointmentOtp> oldOtps = otpRepository.findByTelefonoAndVerificadoFalse(normalized);
        for (AppointmentOtp old : oldOtps) {
            old.setExpiracion(LocalDateTime.now().minusSeconds(1));
        }
        otpRepository.saveAll(oldOtps);

        // Genera PIN de 6 dígitos
        String pin = String.format("%06d", new Random().nextInt(1000000));
        AppointmentOtp otp = AppointmentOtp.builder()
                .telefono(normalized)
                .pin(pin)
                .expiracion(LocalDateTime.now().plusMinutes(10))
                .intentos(0)
                .verificado(false)
                .build();
        otpRepository.save(otp);

        notificationSenderService.sendOtp(normalized, pin);
    }

    @Override
    @Transactional
    public boolean verifyOtp(String telefono, String pin) {
        if (pin == null) {
            throw new IllegalArgumentException("El código PIN no puede ser nulo.");
        }
        String cleanPin = pin.trim().replaceAll("\\D", "");
        String normalized = normalizeTelefono(telefono);
        LocalDateTime now = LocalDateTime.now();

        // 1. Si ya existe un OTP verificado recientemente y sigue vigente, lo tomamos como sesión válida
        Optional<AppointmentOtp> activeSession = otpRepository
                .findFirstByTelefonoAndPinAndVerificadoTrueAndExpiracionAfterOrderByExpiracionDesc(normalized, cleanPin, now);
        if (activeSession.isPresent()) {
            return true;
        }

        // 2. Si no, verificamos un OTP pendiente
        AppointmentOtp otp = otpRepository
                .findFirstByTelefonoAndVerificadoFalseAndExpiracionAfterOrderByExpiracionDesc(normalized, now)
                .orElseThrow(() -> new IllegalArgumentException("Código PIN no válido o expirado. Solicite uno nuevo."));

        if (otp.getIntentos() >= 3) {
            otp.setExpiracion(LocalDateTime.now());
            otpRepository.save(otp);
            throw new IllegalArgumentException("Código bloqueado por superar los 3 intentos fallidos. Solicite uno nuevo.");
        }

        String cleanOtpPin = otp.getPin().trim().replaceAll("\\D", "");
        if (cleanOtpPin.equals(cleanPin)) {
            otp.setVerificado(true); // Se marca como consumido inmediatamente (un solo uso)
            otp.setExpiracion(LocalDateTime.now().plusMinutes(15)); // Extendemos vigencia para que sirva de sesión por 15m
            otpRepository.save(otp);
            return true;
        } else {
            otp.setIntentos(otp.getIntentos() + 1);
            if (otp.getIntentos() >= 3) {
                otp.setExpiracion(LocalDateTime.now()); // Invalidar por intentos
            }
            otpRepository.save(otp);
            throw new IllegalArgumentException("Código PIN incorrecto.");
        }
    }

    @Override
    @Transactional
    public AppointmentDto createPublicAppointment(PublicBookRequest request) {
        String normalized = normalizeTelefono(request.telefono());

        // Validar y consumir el OTP
        verifyOtp(normalized, request.pin());

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

        // Buscar o crear Customer
        Customer customer = customerRepository.findByTelefono(normalized)
                .orElse(null);

        if (customer == null) {
            customer = Customer.builder()
                    .nombre(request.nombre())
                    .telefono(normalized)
                    .build();
        } else {
            customer.setNombre(request.nombre()); // Actualizar nombre si cambia
        }
        customer = customerRepository.save(customer);

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .worker(worker)
                .serviceItem(service)
                .fecha(request.fecha())
                .horaInicio(request.horaInicio())
                .horaFin(horaFin)
                .estado(AppointmentStatus.PENDIENTE)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        notificationSenderService.sendAppointmentConfirmation(saved);
        
        return AppointmentDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByCustomerPhone(String telefono, String pin) {
        String normalized = normalizeTelefono(telefono);
        
        // Validar OTP
        verifyOtp(normalized, pin);

        // Obtener citas futuras activas del cliente
        LocalDate hoy = LocalDate.now();
        List<Appointment> list = appointmentRepository
                .findByCustomerTelefonoAndFechaGreaterThanEqualAndEstadoNot(normalized, hoy, AppointmentStatus.CANCELADA);

        return list.stream()
                .map(AppointmentDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentDto cancelPublicAppointment(UUID id, String telefono, String pin) {
        String normalized = normalizeTelefono(telefono);

        // Validar OTP
        verifyOtp(normalized, pin);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (appointment.getCustomer() == null || !appointment.getCustomer().getTelefono().equals(normalized)) {
            throw new IllegalArgumentException("No tienes permiso para cancelar esta cita.");
        }

        appointment.setEstado(AppointmentStatus.CANCELADA);
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentDto.fromEntity(saved);
    }

    private record TimeBlock(LocalTime start, LocalTime end) {}
}
