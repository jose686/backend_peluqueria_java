package com.peluqueria.backend.staff.controllers;

import com.peluqueria.backend.staff.dtos.RegisterWorkerDto;
import com.peluqueria.backend.staff.dtos.WorkerDto;
import com.peluqueria.backend.staff.services.WorkerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/workers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWorkerController {

    private final WorkerService workerService;

    @Autowired
    public AdminWorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping
    public ResponseEntity<?> registerWorker(@Valid @RequestBody RegisterWorkerDto request) {
        try {
            WorkerDto created = workerService.registerWorker(request);
            return ResponseEntity
                    .created(URI.create("/api/v1/workers/" + created.id()))
                    .body(created);
        } catch (IllegalArgumentException ex) {
            Map<String, String> response = new HashMap<>();
            response.put("error", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
