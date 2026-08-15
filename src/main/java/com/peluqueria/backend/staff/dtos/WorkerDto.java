package com.peluqueria.backend.staff.dtos;

import com.peluqueria.backend.staff.entities.Worker;


import java.util.UUID;

public record WorkerDto(
    UUID id,
    String nombre,
    String especialidad
) {
    public static WorkerDto fromEntity(Worker entity) {
        if (entity == null) return null;
        return new WorkerDto(
            entity.getId(),
            entity.getNombre(),
            entity.getEspecialidad()
        );
    }
}
