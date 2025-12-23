package com.example.roleslz_backend.events.entity;

import com.example.roleslz_backend.BaseEntity.BaseEntity;
import com.example.roleslz_backend.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
public class EventoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long Id;

    @Column(name = "title")
    @NotBlank(message = "campo obrigatório")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "localization")
    private Point localization;

    @Column(name = "endereco_ext")
    @NotBlank(message = "campo obrigatório")
    private String enderecoExtenso;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity organizador;
}
