package com.peluqueria.backend.users.services;

import com.peluqueria.backend.users.dtos.UserDto;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.repositories.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userRepository;

    @Autowired
    public UserServiceImpl(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Recupera y mapea a DTO todos los usuarios activos con rol de cliente.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getClients() {
        return userRepository.findByRoleAndActivoTrue(Role.CLIENT).stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Recupera y mapea a DTO todos los usuarios activos con rol de administrador.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAdmins() {
        return userRepository.findByRoleAndActivoTrue(Role.ADMIN).stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Recupera y mapea a DTO todos los usuarios activos con rol de trabajador (empleado).
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getEmployees() {
        return userRepository.findByRoleAndActivoTrue(Role.WORKER).stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
}
