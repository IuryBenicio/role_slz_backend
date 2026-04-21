package com.example.roleslz_backend.tables.avaliacao.repository;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.avaliacao.repository.AvaliacaoRepository;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AvaliacaoRepositoryTest {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Deve retornar uma avaliação pelo ID do usuário com sucesso")
    void findByUserIdSuccess() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setNome("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password");
        testEntityManager.persist(user);

        EventoEntity evento = new EventoEntity();
        evento.setTitle("Test Event");
        testEntityManager.persist(evento);

        AvaliacaoEntity avaliacao = new AvaliacaoEntity();
        avaliacao.setUser(user);
        avaliacao.setEvento(evento);
        avaliacao.setNota(5);
        testEntityManager.persist(avaliacao);
        testEntityManager.flush();

        // Act
        Optional<AvaliacaoEntity> foundAvaliacao = avaliacaoRepository.findByUserId(user.getId());

        // Assert
        assertThat(foundAvaliacao).isPresent();
        assertThat(foundAvaliacao.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(foundAvaliacao.get().getNota()).isEqualTo(5);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar por um ID de usuário que não existe")
    void findByUserIdNotFound() {
        // Arrange
        long nonExistentUserId = 999L;

        // Act
        Optional<AvaliacaoEntity> foundAvaliacao = avaliacaoRepository.findByUserId(nonExistentUserId);

        // Assert
        assertThat(foundAvaliacao).isNotPresent();
    }
}
