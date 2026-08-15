package com.peluqueria.backend.users.services;

import com.peluqueria.backend.users.dtos.LoginRequest;
import com.peluqueria.backend.users.dtos.LoginResponse;
import com.peluqueria.backend.users.dtos.RegisterRequest;
import com.peluqueria.backend.users.dtos.UserDto;

public interface AuthService {
    /**
     * Registra un nuevo usuario cliente en el sistema.
     */
    UserDto register(RegisterRequest request);

    /**
     * Autentica a un usuario con sus credenciales y devuelve un token JWT.
     */
    LoginResponse login(LoginRequest request);
}
