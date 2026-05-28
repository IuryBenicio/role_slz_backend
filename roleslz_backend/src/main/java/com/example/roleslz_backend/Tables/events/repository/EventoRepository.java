package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.locationtech.jts.geom.Point;
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
    Page<EventoEntity> findAll(Pageable page);
    @EntityGraph(attributePaths = {"confirmacoes"})
    Optional<List<EventoEntity>> findByEstadoEventoAndStartDateBefore(EstadoEvento estadoEvento, LocalDateTime data);
    @EntityGraph(attributePaths = {"confirmacoes"})
    Optional<List<EventoEntity>> findByEstadoEventoAndEndDateBefore(EstadoEvento estadoEvento, LocalDateTime data);

    @Query(value = """
        SELECT 
            e.id AS id, 
            e.title AS title, 
            e.description AS description, 
            e.start_date AS startDate, 
            e.end_date AS endDate,
            e.image_url AS imageUrl,
            e.estado_evento AS estadoEvento,
    
            u.id AS organizadorId, 
            u.nome AS organizadorNome,
            u.avatar AS organizadorAvatar,
    
            s.id AS spotId,
            s.nome AS spotNome,
    
            (SELECT count(*) FROM usuario_evento ue WHERE ue.evento_id = e.id) AS confirmacoesCount,
    
            ST_Y(s.localizacao) AS lat, 
            ST_X(s.localizacao) AS lng,
    
            ST_DistanceSphere(s.localizacao, :posicaoUsuario) AS distancia_metros
    
        FROM evento e
        INNER JOIN spot s ON e.spot_id = s.id
        INNER JOIN usuario u ON e.organizador_id = u.id
        WHERE 
            /* 1. Filtro de Estado do evento */
            e.estado_evento != 'ENCERRADO' 
    
            /* 2. Filtro de Visibilidade */
            AND e.visibilidade = 'PUBLICO'
    
            /* 3. Filtro Espacial (Bounding Box) */
            AND s.localizacao && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
    
            /* 4. Filtro Temporal */
            AND (
                (e.start_date::date = CURRENT_DATE AND e.start_date >= CURRENT_TIMESTAMP)
                OR 
                (CURRENT_TIMESTAMP BETWEEN e.start_date AND e.end_date)
            )
    
        ORDER BY distancia_metros ASC
    """, nativeQuery = true)
    List<EventoComDistanciaProjection> buscarEventosProximos(
            @Param("posicaoUsuario") Point posicaoUsuario,
            @Param("minLon") double minLon,
            @Param("minLat") double minLat,
            @Param("maxLon") double maxLon,
            @Param("maxLat") double maxLat
    );
}
