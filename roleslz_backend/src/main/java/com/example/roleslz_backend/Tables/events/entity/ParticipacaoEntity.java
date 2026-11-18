package com.example.roleslz_backend.Tables.events.entity;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registro PERMANENTE de que um usuário esteve num evento.
 *
 * Diferente de {@link PresencaAtivaEntity} (cache do heatmap, apagado após o evento),
 * esta tabela nunca é limpa: é a fonte de verdade do histórico do usuário e da
 * elegibilidade para avaliar o evento.
 */
@Entity
@Table(
        name = "participacao_evento",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "evento_id"})
)
@Data
@NoArgsConstructor
public class ParticipacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoEntity evento;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @Column(name = "check_out_at")
    private LocalDateTime checkOutAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false)
    private OrigemParticipacao origem;

    public ParticipacaoEntity(UserEntity user, EventoEntity evento, OrigemParticipacao origem) {
        this.user = user;
        this.evento = evento;
        this.origem = origem;
    }

    @PrePersist
    protected void onCreate() {
        if (this.checkInAt == null) {
            this.checkInAt = LocalDateTime.now();
        }
    }
}
