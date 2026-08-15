package com.peluqueria.backend.core;

import com.peluqueria.backend.catalog.entities.ServiceItem;
import com.peluqueria.backend.catalog.repositories.ServiceItemRepository;
import com.peluqueria.backend.staff.entities.Shift;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.ShiftRepository;
import com.peluqueria.backend.staff.repositories.WorkerRepository;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.repositories.UserAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final WorkerRepository workerRepository;
    private final ShiftRepository shiftRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserAccountRepository userRepository,
                           ServiceItemRepository serviceItemRepository,
                           WorkerRepository workerRepository,
                           ShiftRepository shiftRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.workerRepository = workerRepository;
        this.shiftRepository = shiftRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Users
        if (userRepository.count() == 0) {
            UserAccount admin = UserAccount.builder()
                    .email("admin@peluqueria.com")
                    .password(passwordEncoder.encode("1234"))
                    .nombre("Admin")
                    .apellidos("Peluqueria")
                    .telefono("600000001")
                    .role(Role.ADMIN)
                    .activo(true)
                    .build();
            userRepository.save(admin);

            UserAccount cliente = UserAccount.builder()
                    .email("cliente@peluqueria.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .nombre("Cliente")
                    .apellidos("Ejemplo")
                    .telefono("600000002")
                    .role(Role.CLIENT)
                    .activo(true)
                    .build();
            userRepository.save(cliente);
        }

        // 2. Seed ServiceItems
        if (serviceItemRepository.count() == 0) {
            serviceItemRepository.save(ServiceItem.builder()
                    .nombre("Corte de Pelo")
                    .precio(new BigDecimal("18.50"))
                    .duracionMinutos(30)
                    .build());

            serviceItemRepository.save(ServiceItem.builder()
                    .nombre("Mechas Balayage")
                    .precio(new BigDecimal("55.00"))
                    .duracionMinutos(90)
                    .build());

            serviceItemRepository.save(ServiceItem.builder()
                    .nombre("Peinado")
                    .precio(new BigDecimal("25.00"))
                    .duracionMinutos(45)
                    .build());
        }

        // 3. Seed Workers with Shifts
        if (workerRepository.count() == 0) {
            UserAccount workerAccount = UserAccount.builder()
                    .email("12345678A")
                    .password(passwordEncoder.encode("12345678A"))
                    .nombre("Ana Gómez")
                    .role(Role.WORKER)
                    .activo(true)
                    .build();
            workerAccount = userRepository.save(workerAccount);

            Worker worker = Worker.builder()
                    .nombre("Ana Gómez")
                    .dni("12345678A")
                    .especialidad("Corte y Color")
                    .userAccount(workerAccount)
                    .build();
            worker = workerRepository.save(worker);

            // Turno de mañana hoy
            Shift shiftManana = Shift.builder()
                    .worker(worker)
                    .fecha(LocalDate.now())
                    .horaInicio(LocalTime.of(9, 0))
                    .horaFin(LocalTime.of(14, 0))
                    .build();
            shiftManana = shiftRepository.save(shiftManana);

            // Turno de tarde hoy
            Shift shiftTarde = Shift.builder()
                    .worker(worker)
                    .fecha(LocalDate.now())
                    .horaInicio(LocalTime.of(17, 0))
                    .horaFin(LocalTime.of(20, 0))
                    .build();
            shiftRepository.save(shiftTarde);
        }
    }
}
