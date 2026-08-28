package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.ParticipacaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipacaoRepository extends JpaRepository<ParticipacaoEntity, Long> {

    boolean existsByUserIdAndEventoId(Long userId, Long eventoId);

    Optional<ParticipacaoEntity> findByUserIdAndEventoId(Long userId, Long eventoId);

    // Quem esteve num evento — usado pelo scheduler para mirar o push de avaliação.
    List<ParticipacaoEntity> findByEventoId(Long eventoId);

    // Histórico: eventos em que o usuário esteve, do mais recente para o mais antigo.
    @Query("SELECT p.evento FROM ParticipacaoEntity p WHERE p.user.id = :userId ORDER BY p.checkInAt DESC")
    Page<EventoEntity> findEventosParticipadosByUserId(@Param("userId") Long userId, Pageable pageable);
}
