package com.example.roleslz_backend.Tables.avaliacao.entity;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "avaliacao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "evento_id"}) // 1 avaliação por pessoa por evento
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AvaliacaoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "nota", nullable = false)
    private float nota;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoEntity evento;
}
