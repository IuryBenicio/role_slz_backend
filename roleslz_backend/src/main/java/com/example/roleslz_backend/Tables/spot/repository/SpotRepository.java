package com.example.roleslz_backend.Tables.spot.repository;

import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<SpotEntity, Long> {
}
