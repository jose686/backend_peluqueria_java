package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.staff.dtos.WorkerDto;
import com.peluqueria.backend.staff.dtos.WorkerRequest;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.WorkerRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface WorkerService {
    /**
     * Obtiene el listado completo de todos los trabajadores (peluqueros) registrados.
     */
    List<WorkerDto> getAll();

    /**
     * Obtiene el perfil de un trabajador específico por su ID único.
     */
    WorkerDto getById(UUID id);

    /**
     * Registra un nuevo trabajador en el sistema.
     */
    WorkerDto create(WorkerRequest request);

    /**
     * Registra un nuevo trabajador con su respectiva cuenta de usuario.
     */
    WorkerDto registerWorker(com.peluqueria.backend.staff.dtos.RegisterWorkerDto request);

    /**
     * Actualiza la información de un trabajador existente.
     */
    WorkerDto update(UUID id, WorkerRequest request);

    /**
     * Elimina a un trabajador del sistema por su ID único.
     */
    void delete(UUID id);
}
