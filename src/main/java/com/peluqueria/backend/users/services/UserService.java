package com.peluqueria.backend.users.services;

import com.peluqueria.backend.users.dtos.UserDto;
import java.util.List;

public interface UserService {
    /**
     * Obtiene una lista de todos los usuarios registrados con el rol CLIENT que estén activos.
     */
    List<UserDto> getClients();

    /**
     * Obtiene una lista de todos los usuarios registrados con el rol ADMIN que estén activos.
     */
    List<UserDto> getAdmins();

    /**
     * Obtiene una lista de todos los usuarios registrados con el rol WORKER que estén activos.
     */
    List<UserDto> getEmployees();
}
