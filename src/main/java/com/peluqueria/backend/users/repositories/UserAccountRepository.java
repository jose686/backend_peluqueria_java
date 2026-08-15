package com.peluqueria.backend.users.repositories;

import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.entities.UserAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<UserAccount> findByRoleAndActivoTrue(Role role);
}
