# Especificaciones del Proyecto: API REST Peluquería (Java + Angular)

## 1. Visión General y Objetivos
Desarrollo de una plataforma de gestión y reservas para una peluquería, separada en dos aplicaciones:
* **Frontend (Angular):** Interfaz de usuario (SPA). Incluye la parte pública (Home, Catálogo, Blog, Portal de Cliente) y el panel de Administración.
* **Backend (Java / Spring Boot):** API REST encargada de la lógica de negocio, seguridad y acceso a base de datos.
* **Lógica de Reservas:** El sistema calcula huecos cruzando la duración fija de los servicios con la disponibilidad (turnos y descansos) de los trabajadores. Pago presencial en local.

## 2. Stack Tecnológico
* **Backend:** Java, Spring Boot (versión 3+). Solo expone endpoints REST (`@RestController`).
* **Frontend:** Angular (Gestión de rutas, componentes y llamadas HTTP al backend).
* **Seguridad:** Spring Security 6+ configurado como API Stateless (Sin estado). Autenticación obligatoria mediante **Tokens JWT**.
* **Base de Datos:** Spring Data JPA, Hibernate.
* **Validación:** Bean Validation (`jakarta.validation`) en los DTOs de entrada.

## 3. Arquitectura del Backend (Tradicional / Capas)
El proyecto seguirá una estructura clásica por capas (Controllers, Services, Repositories, Entities). 
Se permite y fomenta el uso de relaciones directas de Hibernate (`@ManyToOne`, `@OneToMany`) para facilitar las consultas complejas de reservas.

## 4. Estructura de Base de Datos (Entidades y Relaciones)

* **Usuario (User):** Maneja el acceso (Clientes y Admins).
  * Campos: ID, email, password, Rol (`CLIENT`, `ADMIN`).

* **Servicio (ServiceItem):** El catálogo de lo que se ofrece.
  * Campos: ID, nombre, precio, `duracionMinutos` (valor numérico fijo).

* **Trabajador (Worker):** Entidad gestionada por el Admin.
  * Campos: ID, nombre, especialidad.
  * Relaciones: `@OneToMany` hacia `Shift` y `Appointment`.

* **Turno (Shift):** Horario de trabajo de un empleado en un día.
  * Campos: ID, fecha, horaInicio, horaFin.
  * Relaciones: `@ManyToOne` hacia `Worker`. `@OneToMany` hacia `Break`.

* **Descanso (Break):** Pausas dentro de un turno.
  * Campos: ID, horaInicio, horaFin.
  * Relaciones: `@ManyToOne` hacia `Shift`.

* **Cita (Appointment):** La reserva final.
  * Campos: ID, fecha, horaInicio, horaFin, estado (Pendiente, Completada, Cancelada).
  * Relaciones: `@ManyToOne` hacia `User` (el cliente), `@ManyToOne` hacia `Worker` y `@ManyToOne` hacia `ServiceItem`.

## 5. Lógica del Algoritmo de Reservas
Para ofrecer un hueco a un cliente, el backend (Angular consultando a Spring Boot) debe:
1. Leer la `duracionMinutos` del `ServiceItem`.
2. Buscar los `Shift` (turnos) del `Worker` seleccionado para la fecha indicada.
3. Restar los bloques de tiempo ocupados por los `Break` (descansos) y los `Appointment` (citas ya confirmadas) de ese día.
4. Devolver a Angular un array JSON con las horas de inicio exactas donde el servicio cabe perfectamente.