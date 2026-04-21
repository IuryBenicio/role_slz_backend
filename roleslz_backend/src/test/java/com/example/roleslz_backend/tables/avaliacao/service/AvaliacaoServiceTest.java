package com.example.roleslz_backend.tables.avaliacao.service;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoAlreadyExists;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoDoesntExists;
import com.example.roleslz_backend.Tables.avaliacao.mapper.AvaliacaoMapperClass;
import com.example.roleslz_backend.Tables.avaliacao.repository.AvaliacaoRepository;
import com.example.roleslz_backend.Tables.avaliacao.service.AvaliacaoService;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AvaliacaoMapperClass avaliacaoMapperClass;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private AvaliacaoDTO avaliacaoDTO;
    private AvaliacaoEntity avaliacaoEntity;

    @BeforeEach
    void setUp() {
        UserEntity userTest = new UserEntity();

        EventoEntity eventoTest = new EventoEntity();

        avaliacaoDTO = new AvaliacaoDTO(1L, 5.0f, userTest, eventoTest, null);
        avaliacaoEntity = new AvaliacaoEntity();
        avaliacaoEntity.setId(1L);
        avaliacaoEntity.setNota(5.0f);
    }

    @Test
    @DisplayName("Deve adicionar uma nova avaliação com sucesso")
    void addAvaliacaoSuccess() {
        // Arrange
        long userId = 1L;
        when(avaliacaoRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(avaliacaoMapperClass.toEntity(avaliacaoDTO)).thenReturn(avaliacaoEntity);
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenReturn(avaliacaoEntity);
        when(avaliacaoMapperClass.toDto(avaliacaoEntity)).thenReturn(avaliacaoDTO);

        // Act
        AvaliacaoDTO result = avaliacaoService.addAvaliacao(avaliacaoDTO, userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.nota()).isEqualTo(5.0f);
        verify(avaliacaoRepository, times(1)).findByUserId(userId);
        verify(avaliacaoRepository, times(1)).save(avaliacaoEntity);
    }

    @Test
    @DisplayName("Deve lançar AvaliacaoAlreadyExists ao tentar adicionar uma avaliação para um usuário que já possui uma")
    void addAvaliacaoAlreadyExists() {
        // Arrange
        long userId = 1L;
        when(avaliacaoRepository.findByUserId(userId)).thenReturn(Optional.of(avaliacaoEntity));

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.addAvaliacao(avaliacaoDTO, userId))
                .isInstanceOf(AvaliacaoAlreadyExists.class)
                .hasMessage("Avaliação já existe!");

        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma avaliação com sucesso")
    void deleteAvaliacaoSuccess() {
        // Arrange
        long avaliacaoId = 1L;
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacaoEntity));
        doNothing().when(avaliacaoRepository).delete(avaliacaoEntity);

        // Act
        avaliacaoService.deleteAvaliacao(avaliacaoId);

        // Assert
        verify(avaliacaoRepository, times(1)).findById(avaliacaoId);
        verify(avaliacaoRepository, times(1)).delete(avaliacaoEntity);
    }

    @Test
    @DisplayName("Deve lançar AvaliacaoDoesntExists ao tentar deletar uma avaliação que não existe")
    void deleteAvaliacaoNotFound() {
        // Arrange
        long avaliacaoId = 999L;
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.deleteAvaliacao(avaliacaoId))
                .isInstanceOf(AvaliacaoDoesntExists.class)
                .hasMessage("Avaliação não encontrada para deleção");

        verify(avaliacaoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve editar uma avaliação com sucesso")
    void editAvaliacaoSuccess() {
        UserEntity userTest2 = new UserEntity();

        EventoEntity eventoTest2 = new EventoEntity();

        // Arrange
        long avaliacaoId = 1L;
        float newRate = 4.0f;
        AvaliacaoDTO expectedDto = new AvaliacaoDTO(1L, newRate, userTest2, eventoTest2, null);

        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacaoEntity));
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(avaliacaoMapperClass.toDto(any(AvaliacaoEntity.class))).thenReturn(expectedDto);

        // Act
        AvaliacaoDTO result = avaliacaoService.editAvaliacao(avaliacaoId, newRate);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.nota()).isEqualTo(newRate);
        verify(avaliacaoRepository, times(1)).findById(avaliacaoId);
        verify(avaliacaoRepository, times(1)).save(any(AvaliacaoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar AvaliacaoDoesntExists ao tentar editar uma avaliação que não existe")
    void editAvaliacaoNotFound() {
        // Arrange
        long avaliacaoId = 999L;
        float newRate = 4.0f;
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> avaliacaoService.editAvaliacao(avaliacaoId, newRate))
                .isInstanceOf(AvaliacaoDoesntExists.class)
                .hasMessage("Avaliação não encontrada");

        verify(avaliacaoRepository, never()).save(any());
    }
}
