package com.peluqueria.backend.core;


/*
 
import com.peluqueria.backend.catalog.entities.ServiceItem;
import com.peluqueria.backend.catalog.repositories.ServiceItemRepository;
import com.peluqueria.backend.staff.entities.Shift;
import com.peluqueria.backend.staff.entities.Worker;
import com.peluqueria.backend.staff.repositories.ShiftRepository;
import com.peluqueria.backend.staff.repositories.WorkerRepository;
import com.peluqueria.backend.users.entities.Role;
import com.peluqueria.backend.users.entities.UserAccount;
import com.peluqueria.backend.users.repositories.UserAccountRepository;
import com.peluqueria.backend.appointments.entities.Appointment;
import com.peluqueria.backend.appointments.entities.AppointmentStatus;
import com.peluqueria.backend.appointments.entities.Customer;
import com.peluqueria.backend.appointments.repositories.AppointmentRepository;
import com.peluqueria.backend.appointments.repositories.CustomerRepository;

import com.peluqueria.backend.catalog.entities.Category;
import com.peluqueria.backend.catalog.entities.CategoryType;
import com.peluqueria.backend.catalog.entities.CatalogItem;
import com.peluqueria.backend.catalog.entities.CatalogType;
import com.peluqueria.backend.catalog.repositories.CategoryRepository;
import com.peluqueria.backend.catalog.repositories.CatalogItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final WorkerRepository workerRepository;
    private final ShiftRepository shiftRepository;
    private final CustomerRepository customerRepository;
    private final AppointmentRepository appointmentRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogItemRepository catalogItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserAccountRepository userRepository,
                           ServiceItemRepository serviceItemRepository,
                           WorkerRepository workerRepository,
                           ShiftRepository shiftRepository,
                           CustomerRepository customerRepository,
                           AppointmentRepository appointmentRepository,
                           CategoryRepository categoryRepository,
                           CatalogItemRepository catalogItemRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.workerRepository = workerRepository;
        this.shiftRepository = shiftRepository;
        this.customerRepository = customerRepository;
        this.appointmentRepository = appointmentRepository;
        this.categoryRepository = categoryRepository;
        this.catalogItemRepository = catalogItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Users
        if (userRepository.count() == 0) {
            userRepository.save(UserAccount.builder()
                    .email("admin@peluqueria.com")
                    .password(passwordEncoder.encode("1234"))
                    .nombre("Admin")
                    .apellidos("Peluqueria")
                    .telefono("600000001")
                    .role(Role.ADMIN)
                    .activo(true)
                    .build());

            userRepository.save(UserAccount.builder()
                    .email("cliente@peluqueria.com")
                    .password(passwordEncoder.encode("cliente123"))
                    .nombre("Cliente")
                    .apellidos("Ejemplo")
                    .telefono("600000002")
                    .role(Role.CLIENT)
                    .activo(true)
                    .build());
        }

        // 2. Seed Categories
        Category peluqueriaCat = null;
        Category coloracionCat = null;
        Category tratamientosCat = null;
        Category productosCat = null;

        if (categoryRepository.count() == 0) {
            peluqueriaCat = categoryRepository.save(Category.builder()
                    .nombre("Peluquería")
                    .tipo(CategoryType.CATALOGO)
                    .build());
            coloracionCat = categoryRepository.save(Category.builder()
                    .nombre("Coloración")
                    .tipo(CategoryType.CATALOGO)
                    .build());
            tratamientosCat = categoryRepository.save(Category.builder()
                    .nombre("Tratamientos")
                    .tipo(CategoryType.CATALOGO)
                    .build());
            productosCat = categoryRepository.save(Category.builder()
                    .nombre("Productos")
                    .tipo(CategoryType.CATALOGO)
                    .build());
        } else {
            peluqueriaCat = categoryRepository.findBySlug("peluquería").orElse(null);
            if (peluqueriaCat == null) peluqueriaCat = categoryRepository.findBySlug("peluqueria").orElse(null);
            coloracionCat = categoryRepository.findBySlug("coloración").orElse(null);
            if (coloracionCat == null) coloracionCat = categoryRepository.findBySlug("coloracion").orElse(null);
            tratamientosCat = categoryRepository.findBySlug("tratamientos").orElse(null);
            productosCat = categoryRepository.findBySlug("productos").orElse(null);
        }

        // 3. Seed CatalogItems
        if (catalogItemRepository.count() == 0) {
            catalogItemRepository.save(CatalogItem.builder()
                    .nombre("Corte de Pelo")
                    .precio(new BigDecimal("18.50"))
                    .tipo(CatalogType.SERVICIO)
                    .duracionMinutos(30)
                    .categoria(peluqueriaCat)
                    .activo(true)
                    .build());

            catalogItemRepository.save(CatalogItem.builder()
                    .nombre("Mechas Balayage")
                    .precio(new BigDecimal("55.00"))
                    .tipo(CatalogType.SERVICIO)
                    .duracionMinutos(90)
                    .categoria(coloracionCat)
                    .activo(true)
                    .build());

            catalogItemRepository.save(CatalogItem.builder()
                    .nombre("Peinado")
                    .precio(new BigDecimal("25.00"))
                    .tipo(CatalogType.SERVICIO)
                    .duracionMinutos(45)
                    .categoria(peluqueriaCat)
                    .activo(true)
                    .build());
        }

        // 4. Seed ServiceItems
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
            // Ana Gómez
            UserAccount workerAccountAna = userRepository.save(UserAccount.builder()
                    .email("12345678A")
                    .password(passwordEncoder.encode("12345678A"))
                    .nombre("Ana Gómez")
                    .role(Role.WORKER)
                    .activo(true)
                    .build());

            Worker workerAna = workerRepository.save(Worker.builder()
                    .nombre("Ana Gómez")
                    .dni("12345678A")
                    .especialidad("Corte y Color")
                    .userAccount(workerAccountAna)
                    .build());

            // Carlos Ruiz
            UserAccount workerAccountCarlos = userRepository.save(UserAccount.builder()
                    .email("87654321B")
                    .password(passwordEncoder.encode("87654321B"))
                    .nombre("Carlos Ruiz")
                    .role(Role.WORKER)
                    .activo(true)
                    .build());

            Worker workerCarlos = workerRepository.save(Worker.builder()
                    .nombre("Carlos Ruiz")
                    .dni("87654321B")
                    .especialidad("Caballeros")
                    .userAccount(workerAccountCarlos)
                    .build());

            // Turnos dinámicos recurrentes para los próximos 90 días (3 meses)
            LocalDate startDate = LocalDate.now();
            for (int i = 0; i < 90; i++) {
                LocalDate currentDate = startDate.plusDays(i);
                if (currentDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                    continue; // Peluquería cerrada los domingos
                }
                
                // Ana: 09:00 a 20:00
                shiftRepository.save(Shift.builder()
                        .worker(workerAna)
                        .fecha(currentDate)
                        .horaInicio(LocalTime.of(9, 0))
                        .horaFin(LocalTime.of(20, 0))
                        .build());

                // Carlos: 09:00 a 20:00
                shiftRepository.save(Shift.builder()
                        .worker(workerCarlos)
                        .fecha(currentDate)
                        .horaInicio(LocalTime.of(9, 0))
                        .horaFin(LocalTime.of(20, 0))
                        .build());
            }
        }

        // 4. Seed Customers (Invitados)
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .nombre("Silvia")
                    .telefono("600111222")
                    .build());

            customerRepository.save(Customer.builder()
                    .nombre("Jose R")
                    .telefono("600222333")
                    .build());
        }

        // 5. Seed Appointments
        if (appointmentRepository.count() == 0) {
            Worker workerAna = workerRepository.findAll().stream()
                    .filter(w -> w.getNombre().contains("Ana"))
                    .findFirst().orElse(null);
            Worker workerCarlos = workerRepository.findAll().stream()
                    .filter(w -> w.getNombre().contains("Carlos"))
                    .findFirst().orElse(null);

            ServiceItem corte = serviceItemRepository.findAll().stream()
                    .filter(s -> s.getNombre().contains("Corte"))
                    .findFirst().orElse(null);
            ServiceItem mechas = serviceItemRepository.findAll().stream()
                    .filter(s -> s.getNombre().contains("Mechas"))
                    .findFirst().orElse(null);

            Customer silvia = customerRepository.findAll().stream()
                    .filter(c -> c.getNombre().contains("Silvia"))
                    .findFirst().orElse(null);
            Customer joseR = customerRepository.findAll().stream()
                    .filter(c -> c.getNombre().contains("Jose"))
                    .findFirst().orElse(null);

            UserAccount clienteUser = userRepository.findByEmail("cliente@peluqueria.com").orElse(null);

            // Cita hoy con Ana
            if (workerAna != null && corte != null && silvia != null) {
                appointmentRepository.save(Appointment.builder()
                        .fecha(LocalDate.now())
                        .horaInicio(LocalTime.of(10, 0))
                        .horaFin(LocalTime.of(10, 30))
                        .worker(workerAna)
                        .serviceItem(corte)
                        .estado(AppointmentStatus.PENDIENTE)
                        .customer(silvia)
                        .build());
            }

            // Cita mañana con Carlos
            if (workerCarlos != null && corte != null && joseR != null) {
                appointmentRepository.save(Appointment.builder()
                        .fecha(LocalDate.now().plusDays(1))
                        .horaInicio(LocalTime.of(11, 30))
                        .horaFin(LocalTime.of(12, 0))
                        .worker(workerCarlos)
                        .serviceItem(corte)
                        .estado(AppointmentStatus.PENDIENTE)
                        .customer(joseR)
                        .build());
            }

            // Cita pasado mañana con Ana
            if (workerAna != null && mechas != null && clienteUser != null) {
                appointmentRepository.save(Appointment.builder()
                        .fecha(LocalDate.now().plusDays(2))
                        .horaInicio(LocalTime.of(16, 0))
                        .horaFin(LocalTime.of(17, 30))
                        .worker(workerAna)
                        .serviceItem(mechas)
                        .estado(AppointmentStatus.PENDIENTE)
                        .user(clienteUser)
                        .build());
            }
        }
    }
}
*/