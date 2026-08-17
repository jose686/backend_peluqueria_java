package com.peluqueria.backend.appointments.repositories;

import com.peluqueria.backend.appointments.entities.Appointment;
import com.peluqueria.backend.appointments.entities.AppointmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByUserId(UUID userId);
    List<Appointment> findByWorkerId(UUID workerId);
    List<Appointment> findByWorkerIdAndFecha(UUID workerId, LocalDate fecha);
    List<Appointment> findByWorkerIdAndFechaAndEstadoNot(UUID workerId, LocalDate fecha, AppointmentStatus estado);
    List<Appointment> findByCustomerTelefonoAndFechaGreaterThanEqualAndEstadoNot(String telefono, LocalDate fecha, AppointmentStatus estado);
    List<Appointment> findByEstado(AppointmentStatus estado);
}
