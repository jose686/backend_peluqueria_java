package com.peluqueria.backend.appointments.repositories;

import com.peluqueria.backend.appointments.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByTelefono(String telefono);
}
