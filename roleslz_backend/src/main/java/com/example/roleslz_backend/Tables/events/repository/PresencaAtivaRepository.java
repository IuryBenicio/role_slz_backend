package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.entity.PresencaAtivaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresencaAtivaRepository extends JpaRepository<PresencaAtivaEntity, Long> {

    // Busca se o usuário já fez check-in neste evento
    Optional<PresencaAtivaEntity> findByUserIdAndEventoId(Long userId, Long eventoId);
    boolean existsByUserIdAndEventoId(Long userId, Long eventoId);

    // Metodo super rápido para deletar quando o cara sair do raio do evento (Check-out)
    @Modifying
    @Query("DELETE FROM PresencaAtivaEntity p WHERE p.user.id = :userId AND p.evento.id = :eventoId")
    void deleteByUserIdAndEventoId(@Param("userId") Long userId, @Param("eventoId") Long eventoId);

    // Metodo para a limpeza de madrugada (Job de Faxina)
    @Modifying
    @Query("DELETE FROM PresencaAtivaEntity p WHERE p.evento.id = :eventoId")
    void deleteByEventoId(@Param("eventoId") Long eventoId);

    // QUERY DO HEATMAP: agrega check-ins ativos (tb_presenca_ativa) por spot, dentro da bounding box do mapa.
    // Só considera eventos DURANTE ou PRE começando em até 90 min (mesma janela usada no check-in),
    // e só eventos PUBLICO (eventos privados não podem vazar localização/lotação no heatmap público).
    @Query(value = """
    SELECT
        ST_X(s.localizacao) as lng,
        ST_Y(s.localizacao) as lat,
        COUNT(pa.user_id) as peso
    FROM eventos e
    INNER JOIN spot s ON e.spot_id = s.id
    LEFT JOIN tb_presenca_ativa pa ON e.id = pa.evento_id
    WHERE (e.estado_evento = 'DURANTE' OR (e.estado_evento = 'PRE' AND e.start_date <= NOW() + INTERVAL '90 minutes'))
    AND e.event_privacity = 'PUBLICO'
    AND s.localizacao && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
    GROUP BY s.localizacao
    """, nativeQuery = true)
    List<Object[]> findHeatmapData(@Param("minLon") Double minLon, @Param("minLat") Double minLat,
                                   @Param("maxLon") Double maxLon, @Param("maxLat") Double maxLat);
}