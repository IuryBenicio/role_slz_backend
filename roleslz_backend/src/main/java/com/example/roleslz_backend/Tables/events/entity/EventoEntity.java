package com.example.roleslz_backend.Tables.events.entity;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "eventos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    @NotBlank(message = "campo obrigatório")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(columnDefinition = "geometry(Point, 4326)", name = "local")
    private Point local;

    @Column(name = "endereco_ext")
    @NotBlank(message = "campo obrigatório")
    private String enderecoExtenso;

    @ManyToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity organizador;

    @Column(name="image_url")
    private String imageUrl;

    @Column(name = "price")
    private BigDecimal price;

    @ManyToMany
    @JoinTable(
            name = "eventos_confirmacoes",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> confirmacoes;

    @Column(name = "estado_evento")
    private EstadoEvento estadoEvento;

    @ManyToMany
    @JoinTable(
            name = "eventos_comentarios",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "comentario_id")
    )
    private Set<ComentarioEntity> comentarios;

    @ManyToMany
    @JoinTable(
            name = "eventos_categoria",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private Set<CategoriaEntity> categorias;

    @OneToMany(mappedBy = "evento")
    private Set<AvaliacaoEntity> avaliacoes;
}
