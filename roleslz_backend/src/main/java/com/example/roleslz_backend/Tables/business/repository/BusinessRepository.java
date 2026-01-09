package com.example.roleslz_backend.Tables.business.repository;

import com.example.roleslz_backend.Tables.business.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<BusinessEntity, Long> {
    Optional<BusinessEntity>findByNomeFantasia(String nome);
    Optional<BusinessEntity>findByCnpj(String cpnj);
}
