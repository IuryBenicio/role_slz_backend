package com.example.roleslz_backend.Tables.categoria.repository;

import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, Long> {
    Optional<CategoriaEntity> findByName(String name);
}
