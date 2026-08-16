package com.peluqueria.backend.staff.repositories;

import com.peluqueria.backend.staff.entities.Worker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, UUID> {
    Optional<Worker> findByUserAccountId(UUID userAccountId);
}
