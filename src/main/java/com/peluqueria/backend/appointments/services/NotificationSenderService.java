package com.peluqueria.backend.appointments.services;

import com.peluqueria.backend.appointments.entities.Appointment;

public interface NotificationSenderService {
    void sendOtp(String telefono, String pin);
    void sendAppointmentConfirmation(Appointment appointment);
}
