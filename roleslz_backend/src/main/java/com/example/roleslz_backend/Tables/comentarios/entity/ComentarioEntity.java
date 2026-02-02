package com.example.roleslz_backend.Tables.comentarios.entity;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "comentarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "o comentário precisa de um autor")
    private UserEntity user;

    @Column(name = "title")
    @NotBlank(message = "o comentário precisa de um título")
    private String title;

    @Column(name = "comentario", columnDefinition = "TEXT")
    @NotBlank(message = "o comentário precisa ser feito")
    private String comentario;

    @ManyToMany(mappedBy = "comentarios")
    @NotNull(message = "o comentário precisa de um evento ligado a ele")
    private EventoEntity evento;

}
