package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.HeatPointDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.entity.OrigemParticipacao;
import com.example.roleslz_backend.Tables.events.entity.ParticipacaoEntity;
import com.example.roleslz_backend.Tables.events.entity.PresencaAtivaEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class PresencaAtivaService {

    private final PresencaAtivaRepository presencaRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;
    private final UtillsNotification utillsNotification;

    /** Minutos de permanência dentro da cerca para a presença valer (Dwell Time). */
    @Value("${spotrole.checkin.dwell-minutos:15}")
    private int dwellMinutos;

    /** Folga para relógio do aparelho adiantado. */
    private static final int TOLERANCIA_RELOGIO_MIN = 2;

    /** Acima disso a entrada é velha demais para virar check-in (app que ficou dias offline). */
    private static final int ENTRADA_MAIS_ANTIGA_HORAS = 12;

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

    /**
     * Check-in por geofence. `entradaEmEpochMs` é quando o APARELHO cruzou a cerca — o app
     * segura a chamada até a permanência mínima e só então chama aqui, então a distância
     * entre a entrada e agora precisa ser de pelo menos {@code dwellMinutos}.
     *
     * Nada é gravado antes dessa checagem: quem passou de carro não entra no heatmap nem
     * ganha participação permanente (que é o que libera avaliar o evento).
     */
    @Transactional
    public void processarCheckin(Long userId, Long eventoId, Long entradaEmEpochMs) {
        LocalDateTime entradaEm = validarDwellTime(entradaEmEpochMs);

        var evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventNotFounded("Evento não encontrado"));

        // Validação de tolerância: 90 min antes ou DURANTE
        if (evento.getEstadoEvento() != EstadoEvento.DURANTE &&
                !(evento.getEstadoEvento() == EstadoEvento.PRE && evento.getStartDate().minusMinutes(90).isBefore(LocalDateTime.now()))) {
            throw new IllegalStateException("Fora da área/horário de check-in.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFounded("Usuário não encontrado"));

        // Registro PERMANENTE de participação (histórico + elegibilidade de avaliação).
        // O scheduler nunca apaga isso — ao contrário da tb_presenca_ativa.
        if (!participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            ParticipacaoEntity participacao = new ParticipacaoEntity(user, evento, OrigemParticipacao.GEOFENCE);
            participacao.setCheckInAt(entradaEm); // hora real da chegada, não a do request
            participacaoRepository.save(participacao);
        }

        if (!presencaRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            PresencaAtivaEntity presencaEntity = new PresencaAtivaEntity();
            presencaEntity.setEvento(evento);
            presencaEntity.setUser(user);
            presencaEntity.setDataEntrada(entradaEm);

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
     * Confere o Dwell Time e devolve a hora da entrada já convertida para o fuso do servidor.
     * 400 para entrada malformada/impossível, 409 para permanência curta demais.
     */
    private LocalDateTime validarDwellTime(Long entradaEmEpochMs) {
        if (entradaEmEpochMs == null) {
            throw new IllegalArgumentException(
                    "entradaEmEpochMs é obrigatório: é o instante em que o aparelho entrou na cerca.");
        }

        Instant entrada = Instant.ofEpochMilli(entradaEmEpochMs);
        Instant agora = Instant.now();

        if (entrada.isAfter(agora.plus(Duration.ofMinutes(TOLERANCIA_RELOGIO_MIN)))) {
            throw new IllegalArgumentException("Horário de entrada está no futuro.");
        }
        if (entrada.isBefore(agora.minus(Duration.ofHours(ENTRADA_MAIS_ANTIGA_HORAS)))) {
            throw new IllegalArgumentException("Horário de entrada antigo demais para virar check-in.");
        }

        long minutos = Duration.between(entrada, agora).toMinutes();
        if (minutos < dwellMinutos) {
            throw new IllegalStateException("Permanência de " + minutos + " min é menor que o mínimo de "
                    + dwellMinutos + " min para contar presença.");
        }

        return LocalDateTime.ofInstant(entrada, ZoneId.systemDefault());
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
