package com.example.roleslz_backend.Tables.avaliacao.entity;

import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity
@Table(name = "avaliacao")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AvaliacaoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @NotNull(message = "a avaliação precisa de uma nota")
    @Column(name = "nota", nullable = false)
    private float nota;

    @ManyToOne
    @NotNull(message = "a avaliação precisa de um autor")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
