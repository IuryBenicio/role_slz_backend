package com.example.roleslz_backend.Tables.comentarios.repository;

import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComentariosRepository extends JpaRepository<ComentarioEntity, Long> {

    // Busca o comentário mais recente do usuário em um evento específico
    Optional<ComentarioEntity> findFirstByUserAndEventoOrderByCreatedAtDesc(UserEntity user, EventoEntity evento);

}

