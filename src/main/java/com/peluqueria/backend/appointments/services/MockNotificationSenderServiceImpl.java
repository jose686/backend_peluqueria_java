package com.peluqueria.backend.appointments.services;

import com.peluqueria.backend.appointments.entities.Appointment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.notification.provider", havingValue = "mock", matchIfMissing = true)
@Slf4j
public class MockNotificationSenderServiceImpl implements NotificationSenderService {

    @Override
    public void sendOtp(String telefono, String pin) {
        log.info("[MOCK NOTIFICATION] Enviando OTP {} al telefono {}", pin, telefono);
        System.out.println("**************************************************");
        System.out.println("OTP generado para " + telefono + ": " + pin);
        System.out.println("**************************************************");
    }

    @Override
    public void sendAppointmentConfirmation(Appointment appointment) {
        String clienteNombre = appointment.getCustomer() != null ? appointment.getCustomer().getNombre() : appointment.getUser().getNombre();
        String telefono = appointment.getCustomer() != null ? appointment.getCustomer().getTelefono() : appointment.getUser().getTelefono();
        log.info("[MOCK NOTIFICATION] Confirmación de cita enviada a {} ({}) para el {} a las {}",
                clienteNombre, telefono, appointment.getFecha(), appointment.getHoraInicio());
    }
}
