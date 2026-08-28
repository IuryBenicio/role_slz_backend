package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private PresencaAtivaRepository presencaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserUtils userUtils;

    @Mock
    private UtillsNotification utillsNotification;

    @Mock
    private ParticipacaoRepository participacaoRepository;

    @InjectMocks
    private EventoService eventoService;

    @InjectMocks
    private PresencaAtivaService presencaAtivaService;

    private UserEntity organizadorLogado;
    private UserEntity usuarioComum;
    private EventoEntity eventoMock;

    @BeforeEach
    void setUp() {
        organizadorLogado = new UserEntity();
        organizadorLogado.setId(1L);

        usuarioComum = new UserEntity();
        usuarioComum.setId(2L);

        eventoMock = new EventoEntity();
        eventoMock.setId(100L);
        eventoMock.setTitle("Rolê Teste");
        eventoMock.setOrganizador(organizadorLogado);
        eventoMock.setStartDate(LocalDateTime.now().plusDays(1));
    }

    // --- TESTES DE SEGURANÇA (EVENTO SERVICE) ---

    @Test
    @DisplayName("Deve deletar o evento quando o usuário for o organizador")
    void deleteEvento_ComSucesso_QuandoUsuarioEOrganizador() {
        // Arrange
        when(userUtils.getUserLogado()).thenReturn(organizadorLogado);
        when(eventoRepository.findByOrganizadorIdAndTitle(1L, "Rolê Teste")).thenReturn(Optional.of(eventoMock));

        // Act
        eventoService.deleteEvento("Rolê Teste");

        // Assert
        verify(eventoRepository, times(1)).delete(eventoMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar evento que não lhe pertence (Prevenção IDOR)")
    void deleteEvento_DeveLancarException_QuandoUsuarioNaoEOrganizador() {
        // Arrange
        when(userUtils.getUserLogado()).thenReturn(usuarioComum);
        when(eventoRepository.findByOrganizadorIdAndTitle(2L, "Rolê Teste")).thenReturn(Optional.empty());

        // Act & Assert
        EventNotFounded exception = assertThrows(EventNotFounded.class, () -> {
            eventoService.deleteEvento("Rolê Teste");
        });

        assertEquals("Evento não encontrado", exception.getMessage());
        verify(eventoRepository, never()).delete(any());
    }

    // --- TESTES DE GEOFENCING E CHECK-IN (PRESENCA ATIVA SERVICE) ---

    @Test
    @DisplayName("Deve permitir check-in se o evento estiver no estado DURANTE")
    void processarCheckin_ComSucesso_QuandoEstadoDurante() {
        // Arrange
        eventoMock.setEstadoEvento(EstadoEvento.DURANTE);
        when(eventoRepository.findById(100L)).thenReturn(Optional.of(eventoMock));
        when(presencaRepository.existsByUserIdAndEventoId(2L, 100L)).thenReturn(false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(usuarioComum));

        // Act
        presencaAtivaService.processarCheckin(2L, 100L);

        // Assert
        verify(presencaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve bloquear check-in se o evento for PRE e estiver a mais de 90 minutos")
    void processarCheckin_DeveLancarException_QuandoPreEMaisDe90Minutos() {
        // Arrange
        eventoMock.setEstadoEvento(EstadoEvento.PRE);
        // Evento só começa amanhã
        eventoMock.setStartDate(LocalDateTime.now().plusDays(1));
        when(eventoRepository.findById(100L)).thenReturn(Optional.of(eventoMock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            presencaAtivaService.processarCheckin(2L, 100L);
        });

        assertEquals("Fora da área/horário de check-in.", exception.getMessage());
        verify(presencaRepository, never()).save(any());
    }
}