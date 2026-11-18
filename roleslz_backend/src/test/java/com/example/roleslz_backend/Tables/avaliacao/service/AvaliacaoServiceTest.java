package com.example.roleslz_backend.Tables.avaliacao.service;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoRequestDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoAlreadyExists;
import com.example.roleslz_backend.Tables.avaliacao.repository.AvaliacaoRepository;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock private AvaliacaoRepository avaliacaoRepository;
    @Mock private EventoRepository eventoRepository;
    @Mock private UserRepository userRepository;
    @Mock private ParticipacaoRepository participacaoRepository;
    @Mock private UserUtils userUtils;

    @InjectMocks private AvaliacaoService avaliacaoService;

    private final long userId = 7L;
    private final long eventoId = 100L;
    private UserEntity user;
    private EventoEntity eventoEncerrado;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(userId);
        eventoEncerrado = new EventoEntity();
        eventoEncerrado.setId(eventoId);
        eventoEncerrado.setEstadoEvento(EstadoEvento.ENCERRADO);
    }

    private void logado() {
        when(userUtils.getUserLogado()).thenReturn(user);
    }

    @Test
    void deveCriarAvaliacaoQuandoParticipouEEventoEncerrado() {
        logado();
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoEncerrado));
        when(participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(true);
        when(avaliacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        avaliacaoService.criarAvaliacao(eventoId, new AvaliacaoRequestDTO(4.5f, "  top demais  "));

        verify(avaliacaoRepository).save(any(AvaliacaoEntity.class));
    }

    @Test
    void naoDeveAvaliarSeEventoNaoEncerrado() {
        logado();
        eventoEncerrado.setEstadoEvento(EstadoEvento.DURANTE);
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoEncerrado));

        assertThrows(IllegalStateException.class,
                () -> avaliacaoService.criarAvaliacao(eventoId, new AvaliacaoRequestDTO(5f, null)));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveAvaliarSeNaoTevePresencaConfirmada() {
        logado();
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoEncerrado));
        when(participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> avaliacaoService.criarAvaliacao(eventoId, new AvaliacaoRequestDTO(5f, null)));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveAvaliarDuasVezes() {
        logado();
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoEncerrado));
        when(participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(true);
        when(avaliacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(true);

        assertThrows(AvaliacaoAlreadyExists.class,
                () -> avaliacaoService.criarAvaliacao(eventoId, new AvaliacaoRequestDTO(5f, null)));
    }

    @Test
    void naoDeveAvaliarComNotaForaDaFaixa() {
        assertThrows(IllegalArgumentException.class,
                () -> avaliacaoService.criarAvaliacao(eventoId, new AvaliacaoRequestDTO(6f, null)));
        assertThrows(IllegalArgumentException.class,
                () -> avaliacaoService.criarAvaliacao(eventoId, new AvaliacaoRequestDTO(0f, null)));
    }
}
