package com.peluqueria.backend.users.controllers;

import com.peluqueria.backend.users.dtos.UserDto;
import com.peluqueria.backend.users.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para obtener el listado de todos los clientes (sólo accesible para administradores).
     */
    @GetMapping("/clientes")
    public ResponseEntity<List<UserDto>> getClients() {
        return ResponseEntity.ok(userService.getClients());
    }
}
