package com.example.roleslz_backend.Tables.avaliacao.repository;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<AvaliacaoEntity, Long> {
    Optional<AvaliacaoEntity> findByUserId(long id);
}
