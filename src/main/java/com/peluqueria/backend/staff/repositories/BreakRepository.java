package com.peluqueria.backend.staff.repositories;

import com.peluqueria.backend.staff.entities.Break;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BreakRepository extends JpaRepository<Break, UUID> {
    List<Break> findByShiftId(UUID shiftId);
}
