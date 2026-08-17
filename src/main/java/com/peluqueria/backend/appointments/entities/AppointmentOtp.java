package com.peluqueria.backend.appointments.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String telefono;

    @NotBlank
    @Column(nullable = false)
    private String pin;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime expiracion;

    @Builder.Default
    @Column(nullable = false)
    private boolean verificado = false;

    @Builder.Default
    @Column(nullable = false)
    private int intentos = 0;
}
