package com.example.roleslz_backend.Tables.events.entity;

import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.Set;

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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "local")
    private Point local;

    @Column(name = "endereco_ext")
    @NotBlank(message = "campo obrigatório")
    private String enderecoExtenso;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity organizador;

    @Column(name="image_url")
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "eventos_confirmacoes",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> confirmacoes;
}
