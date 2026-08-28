package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, Long> {

    Optional<EventoEntity> findByTitle(String name);
    Optional<EventoEntity> findByOrganizadorIdAndTitle(long id, String name);
    Page<EventoEntity> findAll(Pageable page);

    // ===================================================================================
    // ⏰ QUERYS DOS SCHEDULLERS (Notificações, Encerramento e Limpeza do Heatmap)
    // ===================================================================================

    // 1. O NOVO: Para notificação na janela exata de 30 minutos (Evita Spam)
    @EntityGraph(attributePaths = {"confirmacoes"})
    Optional<List<EventoEntity>> findByEstadoEventoAndStartDateBetween(EstadoEvento estado, LocalDateTime inicio, LocalDateTime fim);

    // 2. O ATUALIZADO: Para notificação de 5 minutos / em cima da hora
    @EntityGraph(attributePaths = {"confirmacoes"})
    Optional<List<EventoEntity>> findByEstadoEventoAndStartDateBefore(EstadoEvento estadoEvento, LocalDateTime dataHora);

    // Busca genérica de estado (mantida)
    Optional<List<EventoEntity>> findByEstadoEvento(EstadoEvento estadoEvento);

    // Para o scheduler que muda o status para ENCERRADO
    @EntityGraph(attributePaths = {"confirmacoes"})
    Optional<List<EventoEntity>> findByEstadoEventoAndEndDateBefore(EstadoEvento estadoEvento, LocalDateTime dataHora);

    // 3. A CEREJA DO BOLO: Busca só os encerrados que AINDA NÃO FORAM LIMPOS (Evita Memory Leak)
    Optional<List<EventoEntity>> findByEstadoEventoAndEndDateBeforeAndPresencaLimpaFalse(EstadoEvento estado, LocalDateTime dataHora);

    // ===================================================================================
    Optional<EventoEntity> findByIdAndOrganizadorId(Long id, Long organizadorId);

    Page<EventoEntity> findByStartDateBetween(LocalDateTime agora, LocalDateTime fim, Pageable pageable);

    Optional<List<EventoEntity>> findByOrganizadorId(long id);

    // A query de agregação do heatmap vive em PresencaAtivaRepository.findHeatmapData
    // (dados de presença ativa são responsabilidade daquele repositório).

    // 1. QUERY DO GEOFENCE: Uso de CAST seguro para o Hibernate não quebrar com '::'
    @Query(value = """
        SELECT e.* FROM eventos e
        INNER JOIN spot s ON e.spot_id = s.id
        WHERE ST_DWithin(
            CAST(s.localizacao AS geography),
            CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography),
            :raioEmMetros
        )
        AND e.estado_evento IN ('DURANTE', 'PRE')
        """, nativeQuery = true)
    List<EventoEntity> findEventosProximosGeoFence(@Param("lat") Double lat,
                                                   @Param("lng") Double lng,
                                                   @Param("raioEmMetros") Double raioEmMetros);

    // 2. QUERY DO FEED E MAPA: Atualizada para usar o GEOFENCING real!
    @Query(value = """
        SELECT 
            e.id AS id, 
            e.title AS title, 
            e.description AS description, 
            e.start_date AS startDate, 
            e.end_date AS endDate,
            (SELECT p.link FROM evento_galery p WHERE p.evento_id = e.id LIMIT 1) AS imageUrl,            
            e.estado_evento AS estadoEvento,
            u.id AS organizadorId, 
            u.nome AS organizadorNome,
            u.image AS organizadorAvatar,
            s.id AS spotId,
            s.nome AS spotNome,
            
            -- O PULO DO GATO: Conta quem está FISICAMENTE na área de Geofencing agora!
            (SELECT count(pa.id) FROM tb_presenca_ativa pa WHERE pa.evento_id = e.id) AS confirmacoesCount,    
            
            ST_Y(s.localizacao) AS lat, 
            ST_X(s.localizacao) AS lng,
            ST_DistanceSphere(s.localizacao, ST_SetSRID(ST_MakePoint(:centerLng, :centerLat), 4326)) AS distancia_metros
        FROM eventos e
        INNER JOIN spot s ON e.spot_id = s.id
        INNER JOIN users u ON e.user_id = u.id
        WHERE 
            e.estado_evento != 'ENCERRADO' 
            AND e.event_privacity = 'PUBLICO'
            AND ST_Intersects(s.localizacao, ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326))
            AND (
                (CAST(e.start_date AS DATE) = CURRENT_DATE AND e.start_date >= CURRENT_TIMESTAMP)
                OR 
                (CURRENT_TIMESTAMP BETWEEN e.start_date AND e.end_date)
            )
        ORDER BY ST_DistanceSphere(s.localizacao, ST_SetSRID(ST_MakePoint(:centerLng, :centerLat), 4326)) ASC
    """, nativeQuery = true)
    List<EventoComDistanciaProjection> buscarEventosEmUmaArea(
            @Param("centerLng") double centerLng,
            @Param("centerLat") double centerLat,
            @Param("minLon") double minLon,
            @Param("minLat") double minLat,
            @Param("maxLon") double maxLon,
            @Param("maxLat") double maxLat
    );

    // Puxa os eventos futuros onde este utilizador confirmou presença, ordenados pelo mais próximo
    @Query("""
        SELECT e FROM EventoEntity e 
        JOIN e.confirmacoes c 
        WHERE c.id = :userId 
        AND e.estadoEvento != 'ENCERRADO'
        ORDER BY e.startDate ASC
    """)
    Page<EventoEntity> findMinhaAgenda(@Param("userId") Long userId, Pageable pageable);
}