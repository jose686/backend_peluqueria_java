package com.peluqueria.backend.staff.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @NotNull
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Worker worker;

    @Column(name = "break_start_time")
    private LocalTime breakStartTime;

    @Column(name = "break_end_time")
    private LocalTime breakEndTime;

    @Builder.Default
    @OneToMany(mappedBy = "shift", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Break> breaks = new ArrayList<>();
}
