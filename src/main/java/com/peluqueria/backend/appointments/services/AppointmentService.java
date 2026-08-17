package com.peluqueria.backend.appointments.services;

import com.peluqueria.backend.appointments.dtos.AppointmentDto;
import com.peluqueria.backend.appointments.dtos.AppointmentRequest;
import com.peluqueria.backend.appointments.dtos.AvailableSlotsResponse;
import com.peluqueria.backend.appointments.dtos.PublicBookRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    /**
     * Obtiene los horarios disponibles para un trabajador, un servicio y una fecha específica.
     */
    AvailableSlotsResponse getAvailableSlots(UUID workerId, UUID serviceItemId, LocalDate fecha);

    /**
     * Obtiene los días disponibles (con al menos un hueco libre) en un rango de fechas.
     */
    java.util.Map<LocalDate, Boolean> getAvailableDaysRange(UUID workerId, UUID serviceItemId, LocalDate startDate, LocalDate endDate);

    /**
     * Crea una nueva cita para el usuario especificado.
     */
    AppointmentDto createAppointment(AppointmentRequest request, String email);

    /**
     * Obtiene la lista de citas asociadas a un usuario según su correo electrónico.
     */
    List<AppointmentDto> getAppointmentsByUser(String email);

    /**
     * Obtiene el detalle de una cita específica por su identificador único.
     */
    AppointmentDto getAppointmentById(UUID id);

    /**
     * Cancela una cita si el usuario tiene los permisos adecuados (es propietario o administrador).
     */
    AppointmentDto cancelAppointment(UUID id, String email);

    /**
     * Envía un código OTP al número de teléfono especificado, invalidando códigos anteriores.
     */
    void sendOtp(String telefono);

    /**
     * Verifica si el código OTP para un teléfono es válido.
     */
    boolean verifyOtp(String telefono, String pin);

    /**
     * Crea una cita pública asociándola a un Customer tras validar el OTP.
     */
    AppointmentDto createPublicAppointment(PublicBookRequest request);

    /**
     * Obtiene las citas futuras de un cliente tras validar el OTP.
     */
    List<AppointmentDto> getAppointmentsByCustomerPhone(String telefono, String pin);

    /**
     * Cancela una cita pública tras validar el OTP.
     */
    AppointmentDto cancelPublicAppointment(UUID id, String telefono, String pin);

    /**
     * Elimina permanentemente una cita por su identificador único.
     */
    void deleteAppointment(UUID id);
}
