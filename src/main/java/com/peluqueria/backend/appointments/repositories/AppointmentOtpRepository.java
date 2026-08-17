package com.peluqueria.backend.appointments.repositories;

import com.peluqueria.backend.appointments.entities.AppointmentOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentOtpRepository extends JpaRepository<AppointmentOtp, UUID> {
    Optional<AppointmentOtp> findFirstByTelefonoAndVerificadoFalseAndExpiracionAfterOrderByExpiracionDesc(String telefono, java.time.LocalDateTime now);
    List<AppointmentOtp> findByTelefonoAndVerificadoFalse(String telefono);
}
