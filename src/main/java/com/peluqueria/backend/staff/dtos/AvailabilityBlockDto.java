package com.peluqueria.backend.staff.dtos;

import java.time.LocalTime;
import java.util.UUID;
import com.peluqueria.backend.appointments.entities.Appointment;

public record AvailabilityBlockDto(
    LocalTime horaInicio,
    LocalTime horaFin,
    boolean disponible,
    boolean descanso,
    AppointmentAdminDto appointment
) {
    public record AppointmentAdminDto(
        UUID id,
        String fechaHora, // Formato "YYYY-MM-DDTHH:mm:ss"
        String estado,
        String notas,
        ClienteDto cliente,
        ServicioDto servicio
    ) {
        public record ClienteDto(UUID id, String nombre, String email, String telefono) {}
        public record ServicioDto(Long id, String nombre, int duracionMinutos, double precio) {}

        public static AppointmentAdminDto fromEntity(Appointment appointment) {
            if (appointment == null) return null;

            String timeStr = appointment.getHoraInicio().toString();
            if (timeStr.length() == 5) {
                timeStr += ":00";
            }
            String fechaHora = appointment.getFecha().toString() + "T" + timeStr;

            ClienteDto cliente;
            if (appointment.getUser() != null) {
                String apellidos = appointment.getUser().getApellidos();
                String nombreCompleto = appointment.getUser().getNombre() + (apellidos != null && !apellidos.isEmpty() ? " " + apellidos : "");
                cliente = new ClienteDto(
                    appointment.getUser().getId(),
                    nombreCompleto,
                    appointment.getUser().getEmail(),
                    appointment.getUser().getTelefono()
                );
            } else if (appointment.getCustomer() != null) {
                cliente = new ClienteDto(
                    appointment.getCustomer().getId(),
                    appointment.getCustomer().getNombre(),
                    "",
                    appointment.getCustomer().getTelefono()
                );
            } else {
                cliente = new ClienteDto(null, "Cliente Invitado", "", "");
            }

            ServicioDto servicio = new ServicioDto(
                appointment.getServiceItem().getId(),
                appointment.getServiceItem().getNombre(),
                appointment.getServiceItem().getDuracionMinutos() != null ? appointment.getServiceItem().getDuracionMinutos() : 30,
                appointment.getServiceItem().getPrecio().doubleValue()
            );

            return new AppointmentAdminDto(
                appointment.getId(),
                fechaHora,
                appointment.getEstado().name(),
                "", // No hay campo 'notas' en la entidad
                cliente,
                servicio
            );
        }
    }
}
