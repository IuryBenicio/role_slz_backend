package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.HeatPointDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.PresencaAtivaEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PresencaAtivaService {

    private final PresencaAtivaRepository presencaRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;

    public PresencaAtivaService(PresencaAtivaRepository presencaRepository,
                                EventoRepository eventoRepository,
                                UserRepository userRepository) {
        this.presencaRepository = presencaRepository;
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
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

        if (!presencaRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            UserEntity user = userRepository.findById(userId).orElseThrow();
            PresencaAtivaEntity presencaEntity = new PresencaAtivaEntity();
            presencaEntity.setEvento(evento);
            presencaEntity.setUser(user);

            presencaRepository.save(presencaEntity);
        }
    }

    /**
     * Remove a presença (Check-out).
     */
    @Transactional
    public void processarCheckout(Long userId, Long eventoId) {
        presencaRepository.deleteByUserIdAndEventoId(userId, eventoId);
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