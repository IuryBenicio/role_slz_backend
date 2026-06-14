package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.entity.PhotosEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotosEventoRepository extends JpaRepository<PhotosEventoEntity, Long> {
}
