package com.example.roleslz_backend.Tables.events.entity;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@ToString
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

//    Endereço escrito por extenso sem ser baseado em localização Lat e Long
    @Column(name = "endereco_ext")
    @NotBlank(message = "campo obrigatório")
    private String enderecoExtenso;

//    Tipo de evento (coorporativo ou pessoal)
    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

//    Privacidade do evento(público ou privado)
    @Column(name = "event_privacity")
    @Enumerated(EnumType.STRING)
    private EventPrivacity eventPrivacity;

//    Usuário responsável pelo evento
    @ManyToOne()
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity organizador;

//    Opcional(pagamento ainda será implementado em uma versão 1.2)
    @Column(name = "price")
    private BigDecimal price;

//    Spot é a tabela referente ao local do evento (que pode estar ligado a uma pessoa ou business)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "spot_id")
    private SpotEntity spot;

//    Campo para verificaçao de presençao de heatmap
    @Column(name = "presenca_limpa", nullable = false)
    private Boolean presencaLimpa = false;

//    Imagens do evento
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotosEventoEntity> images;

//    Confirmações de usuários ara comparecer ao evento
    @ManyToMany
    @JoinTable(
            name = "eventos_confirmacoes",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> confirmacoes;

    @Column(name = "estado_evento")
    @Enumerated(EnumType.STRING)
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

    @Column(name = "exibir_confirmacoes_publicamente", nullable = false)
    private Boolean exibirConfirmacoesPublicamente = true;

    @OneToMany(mappedBy = "evento")
    private Set<AvaliacaoEntity> avaliacoes;
}
