package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.HeatPointDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.OrigemParticipacao;
import com.example.roleslz_backend.Tables.events.entity.ParticipacaoEntity;
import com.example.roleslz_backend.Tables.events.entity.PresencaAtivaEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PresencaAtivaService {

    private final PresencaAtivaRepository presencaRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;
    private final UtillsNotification utillsNotification;

    public PresencaAtivaService(PresencaAtivaRepository presencaRepository,
                                ParticipacaoRepository participacaoRepository,
                                EventoRepository eventoRepository,
                                UserRepository userRepository,
                                UtillsNotification utillsNotification) {
        this.presencaRepository = presencaRepository;
        this.participacaoRepository = participacaoRepository;
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
        this.utillsNotification = utillsNotification;
    }

    @Transactional
    public void processarCheckin(Long userId, Long eventoId) {
        var evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

        // Validação de tolerância: 90 min antes ou DURANTE
        if (evento.getEstadoEvento() != EstadoEvento.DURANTE &&
                !(evento.getEstadoEvento() == EstadoEvento.PRE && evento.getStartDate().minusMinutes(90).isBefore(LocalDateTime.now()))) {
            throw new RuntimeException("Fora da área/horário de check-in.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow();

        // Registro PERMANENTE de participação (histórico + elegibilidade de avaliação).
        // O scheduler nunca apaga isso — ao contrário da tb_presenca_ativa.
        if (!participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            participacaoRepository.save(new ParticipacaoEntity(user, evento, OrigemParticipacao.GEOFENCE));
        }

        if (!presencaRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            PresencaAtivaEntity presencaEntity = new PresencaAtivaEntity();
            presencaEntity.setEvento(evento);
            presencaEntity.setUser(user);

            presencaRepository.save(presencaEntity);

            // Nudge best-effort: "rolê bombando perto de você". Falha de push NUNCA pode
            // abortar o check-in (que alimenta o heatmap).
            if (user.getFcmToken() != null) {
                try {
                    utillsNotification.notificarUsuarioProximoGeofence(evento, user.getFcmToken());
                } catch (Exception e) {
                    log.error("Falha ao notificar proximidade do evento ID {} para o user ID {}", eventoId, userId, e);
                }
            }
        }
    }

    /**
     * Remove a presença ativa (Check-out do heatmap) e marca a hora de saída no registro
     * permanente de participação — que NÃO é apagado.
     */
    @Transactional
    public void processarCheckout(Long userId, Long eventoId) {
        presencaRepository.deleteByUserIdAndEventoId(userId, eventoId);
        participacaoRepository.findByUserIdAndEventoId(userId, eventoId).ifPresent(p -> {
            p.setCheckOutAt(LocalDateTime.now());
            participacaoRepository.save(p);
        });
    }

    /**
     * Dados agregados para o heatmap: um ponto por spot com check-ins ativos dentro
     * da bounding box do viewport do mapa, já filtrado para eventos públicos.
     */
    public List<HeatPointDTO> getHeatmapData(Double minLat, Double minLon, Double maxLat, Double maxLon) {
        List<Object[]> rows = presencaRepository.findHeatmapData(minLon, minLat, maxLon, maxLat);

        return rows.stream()
                .map(row -> new HeatPointDTO(
                        ((Number) row[1]).doubleValue(), // lat
                        ((Number) row[0]).doubleValue(), // lng
                        ((Number) row[2]).longValue()    // peso
                ))
                .toList();
    }
}
