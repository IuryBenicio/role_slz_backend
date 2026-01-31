package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, Long> {
    Optional<EventoEntity> findByTitle(String name);
    Optional<List<EventoEntity>> findByEstadoEventoAndStartDateBefore(EstadoEvento estadoEvento, LocalDateTime data);
}
