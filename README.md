# ☕ Peluquería API REST - Backend (Spring Boot 3 + Java 21)

API RESTful stateless encargada de la lógica de negocio, seguridad JWT, gestión de disponibilidad de personal, catálogo de servicios y persistencia de datos para la plataforma de reservas de peluquería.

---

## 🚀 Visión General y Arquitectura

El backend está diseñado bajo una arquitectura limpia y modular **Package-by-Feature**, desacoplado del frontend Angular y empaquetado para despliegue en entornos Dockerizados.

### Principios Arquitectónicos:
- **Package-by-Feature:** Organización en módulos independientes por dominio funcional (`users`, `staff`, `catalog`, `appointments`, `blog`, `media`, `setup`, `core`).
- **Separación Interface / Implementación:** Todo servicio define una interfaz y su implementación correspondiente en una clase con el sufijo `Impl` (ej. `AppointmentServiceImpl`).
- **Seguridad Stateless (Spring Security 6 + JWT):** Autenticación y autorización basada en tokens JWT signed con HMAC-SHA256, sin mantener sesión en servidor.
- **Modelo de Relaciones ORM:** Hibernate/JPA con identificadores UUID y relaciones directas (`@ManyToOne`, `@OneToMany`) para consultas eficientes.

---

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Descripción |
| :--- | :--- | :--- |
| **Lenguaje** | Java 21 LTS | Runtime moderno con soporte para características de alto rendimiento. |
| **Framework Core** | Spring Boot 3.3.2 | Framework base para creación de APIs REST productivas. |
| **Seguridad** | Spring Security 6 & JJWT 0.12.5 | Autenticación basada en JSON Web Tokens. |
| **Persistencia** | Spring Data JPA / Hibernate | Abstracción de acceso a datos y mapeo objeto-relacional. |
| **Base de Datos** | MySQL 8.0 / PostgreSQL | Motor relacional de base de datos. |
| **Utilidades** | Lombok, Dotenv-java 3.0.0, JSoup 1.18.1 | Reducción de boilerplate, carga de variables de entorno y saneamiento HTML. |
| **Testing & Cobertura** | JUnit 5, Spring Security Test, JaCoCo 0.8.12 | Pruebas unitarias e informes de cobertura de código. |

---

## ✨ Características Principales y Lógica de Negocio

1. **Algoritmo de Cálculo de Disponibilidad y Reservas:**
   - Lee la `duracionMinutos` (fija) del servicio solicitado (`ServiceItem`).
   - Consulta los turnos de trabajo (`Shift`) asignados al empleado (`Worker`) en la fecha indicada.
   - Resta los intervalos de tiempo bloqueados por los descansos (`Break`) y las citas ya confirmadas (`Appointment`).
   - Retorna un listado en JSON con los bloques horarios de inicio exactos donde el servicio cabe sin solapamientos.

2. **Gestión de Usuarios y Roles:**
   - Soporte para roles `CLIENT`, `WORKER` y `ADMIN`.
   - Registro y autenticación mediante JWT.

3. **Módulo de Catálogo y Blog:**
   - Servicios de peluquería organizados por categorías con precio y duración.
   - Artículos de blog con gestión de imágenes y URLs amigables (`slugs`).

4. **Gestión Multimedia (`media`):**
   - Subida y almacenamiento seguro de imágenes para servicios, publicaciones y avatares.

5. **First-Time Setup Assistant (`/api/v1/setup`):**
   - Endpoint de verificación e inicialización que permite la creación del primer usuario `ADMIN` en instalaciones limpias.

---

## 📁 Estructura del Proyecto (Package-by-Feature)

```text
src/main/java/com/peluqueria/backend/
├── appointments/          # Citas y Algoritmo de Disponibilidad
│   ├── controllers/       # Controller de citas y endpoint público de huecos
│   ├── dtos/              # DTOs de entrada/salida
│   ├── entities/          # Entidad Appointment
│   ├── repositories/      # JPA Repository de Citas
│   └── services/          # Servicio e Implementación de reservas y cálculo de huecos
├── blog/                  # Publicaciones y Categorías del Blog
├── catalog/               # Servicios de Peluquería y Categorías
├── core/                  # Seguridad JWT, Filtro Bearer, Manejo Global de Excepciones y Config
├── media/                 # Gestión y almacenamiento de archivos multimedia
├── setup/                 # Asistente de comprobación e inicialización del primer Admin
├── staff/                 # Trabajadores (Worker), Turnos (Shift) y Pausas (Break)
├── users/                 # Autenticación, Registro y Entidad UserAccount
└── BackendApplication.java# Clase principal de arranque de Spring Boot
```

---

## 🚦 Puesta en Marcha Local

### Requisitos Previos
- **Java JDK 21** instalado.
- **Maven 3.8+** (o usar el wrapper `./mvnw`).
- **MySQL 8.0** en ejecución (o contenedor Docker).

---

### Configuración de Variables de Entorno (`.env`)

Crea un archivo `.env` en la raíz del proyecto backend basado en el archivo `.env.example`:

```env
# Configuración de Base de Datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=peluqueria_db
DB_USERNAME=peluqueria_user
DB_PASSWORD=peluqueria_pass
DB_ROOT_PASSWORD=peluqueria_root_pass

# Seguridad / JWT
JWT_SECRET=tu_clave_secreta_super_segura_de_al_menos_256_bits_aqui
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:8082,http://127.0.0.1:8082
```

---

### Opción A: Despliegue Completo con Docker Compose (Recomendado)

Ejecuta el comando Docker Compose en la raíz del repositorio backend para levantar la Base de Datos MySQL, la API Backend y el Frontend Angular simultáneamente:

```bash
docker-compose up -d --build
```

- **API REST Backend:** `http://localhost:8083` (mapeado al contenedor en puerto `8080`).
- **MySQL DB:** Puerto `3306`.
- **Frontend Angular:** `http://localhost:8082`.

---

### Opción B: Ejecución Independiente en Desarrollo

1. Asegúrate de tener una instancia de MySQL corriendo con la base de datos especificada en `.env`.
2. Compilar e iniciar la aplicación mediante Maven:
   ```bash
   ./mvnw clean spring-boot:run
   ```
3. La API estará disponible en `http://localhost:8080`.

---

## 🔌 Endpoints REST Principales

### Autenticación y Setup
| Método | Endpoint | Descripción | Acceso |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Iniciar sesión y obtener Token JWT. | Público |
| `POST` | `/api/v1/auth/register` | Registro de nuevos clientes. | Público |
| `GET` | `/api/v1/setup/status` | Comprobar si se requiere la inicialización de admin. | Público |
| `POST` | `/api/v1/setup/initialize` | Creación del primer usuario Administrador. | Solo si no hay Admins |

### Citas y Disponibilidad
| Método | Endpoint | Descripción | Acceso |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/admin/availability` | Calcular huecos disponibles según trabajador, fecha y servicio. | Autenticado |
| `POST` | `/api/v1/appointments` | Agendar una nueva cita. | Autenticado |
| `GET` | `/api/v1/appointments` | Consultar citas del usuario o globales. | Autenticado |

### Personal y Turnos (Staff)
| Método | Endpoint | Descripción | Acceso |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/workers` | Obtener listado de personal técnico activo. | Público |
| `POST` | `/api/v1/admin/workers` | Registrar nuevo trabajador. | `ROLE_ADMIN` |
| `POST` | `/api/v1/admin/shifts` | Asignar turnos de trabajo y descansos. | `ROLE_ADMIN` |

### Catálogo y Blog
| Método | Endpoint | Descripción | Acceso |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/services` | Listado público de servicios y precios. | Público |
| `POST` | `/api/v1/catalog` | Crear/Modificar elemento del catálogo. | `ROLE_ADMIN` |
| `GET` | `/api/v1/blog` | Listar publicaciones del blog. | Público |
| `POST` | `/api/v1/blog` | Crear nueva publicación en el blog. | `ROLE_ADMIN` |
