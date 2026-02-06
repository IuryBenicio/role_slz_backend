package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, Long> {
    Optional<EventoEntity> findByTitle(String name);
    Optional<List<EventoEntity>> findByEstadoEventoAndStartDateBefore(EstadoEvento estadoEvento, LocalDateTime data);

    //busca por eventos próximos + com a adição das distancias
    @Query(value = """
        SELECT e.*, 
               ST_DistanceSphere(s.localizacao, :posicaoUsuario) as distancia_metros
        FROM evento e
        INNER JOIN spot s ON e.spot_id = s.id
        WHERE ST_DWithin(s.localizacao, :posicaoUsuario, :raioMetros, true)
        AND e.data_hora >= CURRENT_TIMESTAMP
        ORDER BY distancia_metros ASC
        """, nativeQuery = true)
    List<EventoComDistanciaProjection> findEventosComDistancia(Point posicaoUsuario, Double raioMetros);

    //encontra eventos no mapa baseado
    @Query(value = """
        SELECT e.*, 
               ST_DistanceSphere(s.localizacao, :centroMapa) as distancia_metros
        FROM evento e
        INNER JOIN spot s ON e.spot_id = s.id
        WHERE s.localizacao && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
        AND e.start_date >= CURRENT_TIMESTAMP
        """, nativeQuery = true)
    List<EventoComDistanciaProjection> findEventosNoMapa(
            Double minLon, Double minLat,
            Double maxLon, Double maxLat,
            Point centroMapa);
}
