package com.peluqueria.backend.staff.services;

import com.peluqueria.backend.staff.dtos.WorkerDto;
import com.peluqueria.backend.staff.dtos.WorkerRequest;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.WorkerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.repositories.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.peluqueria.backend.staff.dtos.RegisterWorkerDto;

@Service
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public WorkerServiceImpl(WorkerRepository workerRepository, 
                             UserAccountRepository userAccountRepository, 
                             PasswordEncoder passwordEncoder) {
        this.workerRepository = workerRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Recupera y mapea a DTO todos los trabajadores registrados.
     */
    @Transactional(readOnly = true)
    public List<WorkerDto> getAll() {
        return workerRepository.findAll().stream()
                .map(WorkerDto::fromEntity)
                .toList();
    }

    /**
     * Busca un trabajador por su UUID.
     */
    @Transactional(readOnly = true)
    public WorkerDto getById(UUID id) {
        Worker entity = workerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        return WorkerDto.fromEntity(entity);
    }

    /**
     * Guarda un nuevo trabajador en el sistema.
     */
    @Transactional
    public WorkerDto create(WorkerRequest request) {
        Worker entity = Worker.builder()
                .nombre(request.nombre())
                .especialidad(request.especialidad())
                .build();
        return WorkerDto.fromEntity(workerRepository.save(entity));
    }

    /**
     * Modifica los datos de un trabajador existente.
     */
    @Transactional
    public WorkerDto update(UUID id, WorkerRequest request) {
        Worker entity = workerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        entity.setNombre(request.nombre());
        entity.setEspecialidad(request.especialidad());
        return WorkerDto.fromEntity(workerRepository.save(entity));
    }

    /**
     * Elimina el registro de un trabajador de la base de datos.
     */
    @Transactional
    public void delete(UUID id) {
        Worker entity = workerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        workerRepository.delete(entity);
    }

    /**
     * Registra un nuevo trabajador con su respectiva cuenta de usuario.
     */
    @Transactional
    public WorkerDto registerWorker(RegisterWorkerDto request) {
        if (userAccountRepository.existsByEmail(request.dni())) {
            throw new IllegalArgumentException("El DNI ya está registrado en el sistema.");
        }

        UserAccount userAccount = UserAccount.builder()
                .email(request.dni())
                .password(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .role(Role.WORKER)
                .activo(true)
                .build();
        UserAccount savedAccount = userAccountRepository.save(userAccount);

        // Crear el perfil del trabajador
        Worker worker = Worker.builder()
                .dni(request.dni())
                .nombre(request.nombre())
                .especialidad(request.especialidad())
                .userAccount(savedAccount)
                .build();
        Worker savedWorker = workerRepository.save(worker);

        return WorkerDto.fromEntity(savedWorker);
    }
}
