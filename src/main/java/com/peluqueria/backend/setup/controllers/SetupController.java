package com.peluqueria.backend.setup.controllers;

import com.peluqueria.backend.setup.dtos.InitialAdminRequest;
import com.peluqueria.backend.setup.dtos.SetupStatusResponse;
import com.peluqueria.backend.setup.services.SetupService;
import com.peluqueria.backend.users.entities.UserAccount;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/setup")
public class SetupController {

    private final SetupService setupService;

    @Autowired
    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    @GetMapping("/status")
    public ResponseEntity<SetupStatusResponse> getStatus() {
        return ResponseEntity.ok(new SetupStatusResponse(setupService.isSetupRequired()));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody InitialAdminRequest request) {
        try {
            UserAccount admin = setupService.createInitialAdmin(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Superadministrador inicial creado con éxito");
            response.put("email", admin.getEmail());
            response.put("nombre", admin.getNombre());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }
}
