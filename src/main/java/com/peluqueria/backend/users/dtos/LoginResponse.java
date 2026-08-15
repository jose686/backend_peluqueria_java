package com.peluqueria.backend.users.dtos;

import java.util.UUID;

public record LoginResponse(
    String token,
    String type,
    UUID id,
    String email,
    String role
) {
    public LoginResponse(String token, UUID id, String email, String role) {
        this(token, "Bearer", id, email, role);
    }
}
