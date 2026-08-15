package com.peluqueria.backend.staff.repositories;

import com.peluqueria.backend.staff.entities.Shift;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByWorkerIdAndFecha(UUID workerId, LocalDate fecha);

    List<Shift> findByWorkerId(UUID workerId);

    List<Shift> findByFechaBetween(LocalDate start, LocalDate end);
}
