package com.example.roleslz_backend.Tables.events.entity;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "tb_presenca_ativa",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "evento_id"}) // Impede o usuário de ter 2 check-ins ativos no mesmo evento
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresencaAtivaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private EventoEntity evento;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @PrePersist
    protected void onCreate() {
        this.dataEntrada = LocalDateTime.now();
    }
}