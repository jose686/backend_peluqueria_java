package com.peluqueria.backend.users.dtos;

import com.peluqueria.backend.users.entities.UserAccount;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String nombre,
    String apellidos,
    String telefono,
    String role,
    Boolean activo,
    LocalDateTime fechaCreacion
) {
    public static UserDto fromEntity(UserAccount user) {
        if (user == null) return null;
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getNombre(),
            user.getApellidos(),
            user.getTelefono(),
            user.getRole().name(),
            user.getActivo(),
            user.getFechaCreacion()
        );
    }
}
