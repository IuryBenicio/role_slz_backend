package com.example.roleslz_backend.events.repository;

import com.example.roleslz_backend.events.entity.EventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<EventoEntity, Long> {
    EventoEntity findByName(String name);
}
