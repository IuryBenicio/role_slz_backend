package com.example.roleslz_backend.Tables.users.repository;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserProjectionProfile;
import com.example.roleslz_backend.Tables.users.entity.AuthProvider;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByVerificationToken(String token);
    List<EventoEntity> findByEventos_Id(long id);
    Page<UserEntity> findAll(Pageable page);
    Optional<UserProjectionProfile> findByNome(String nome);

    Optional<UserEntity> findByAuthProviderAndProviderId(AuthProvider provider, String providerId);
}
