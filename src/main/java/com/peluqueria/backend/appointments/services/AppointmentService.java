package com.peluqueria.backend.appointments.services;

import com.peluqueria.backend.appointments.dtos.AppointmentDto;
import com.peluqueria.backend.appointments.dtos.AppointmentRequest;
import com.peluqueria.backend.appointments.dtos.AvailableSlotsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    /**
     * Obtiene los horarios disponibles para un trabajador, un servicio y una fecha específica.
     */
    AvailableSlotsResponse getAvailableSlots(UUID workerId, UUID serviceItemId, LocalDate fecha);

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
     * Elimina permanentemente una cita por su identificador único.
     */
    void deleteAppointment(UUID id);
}
