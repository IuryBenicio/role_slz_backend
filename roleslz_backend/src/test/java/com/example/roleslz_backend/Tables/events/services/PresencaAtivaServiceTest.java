package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.ParticipacaoEntity;
import com.example.roleslz_backend.Tables.events.entity.PresencaAtivaEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaAtivaServiceTest {

    @Mock
    private PresencaAtivaRepository presencaAtivaRepository;
    @Mock
    private ParticipacaoRepository participacaoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private UtillsNotification utillsNotification;

    @InjectMocks
    private PresencaAtivaService presencaAtivaService;

    private static final int DWELL_MIN = 15;

    @BeforeEach
    void configurarDwell() {
        // @Value não é resolvido em teste de Mockito puro.
        ReflectionTestUtils.setField(presencaAtivaService, "dwellMinutos", DWELL_MIN);
    }

    /** Entrada na cerca há N minutos, no formato que o app manda (epoch em ms). */
    private long entradaHaMinutos(int minutos) {
        return Instant.now().minusSeconds(minutos * 60L).toEpochMilli();
    }

    private EventoEntity eventoAtivo(Long eventoId) {
        EventoEntity evento = new EventoEntity();
        evento.setId(eventoId);
        evento.setTitle("Rolê Teste");
        evento.setEstadoEvento(EstadoEvento.DURANTE);
        evento.setStartDate(LocalDateTime.now().minusHours(1));
        evento.setEndDate(LocalDateTime.now().plusHours(2));
        return evento;
    }

    @Test
    void deveProcessarCheckinComSucessoQuandoUsuarioEEventoExistem() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(presencaAtivaRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);

        presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(DWELL_MIN + 5));

        verify(presencaAtivaRepository, times(1)).save(any(PresencaAtivaEntity.class));
    }

    @Test
    void deveGravarParticipacaoPermanenteNoCheckin() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);

        presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(DWELL_MIN + 5));

        verify(participacaoRepository, times(1)).save(any(ParticipacaoEntity.class));
    }

    @Test
    void naoDeveDuplicarParticipacaoSeJaExiste() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(true);

        presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(DWELL_MIN + 5));

        verify(participacaoRepository, never()).save(any(ParticipacaoEntity.class));
    }

    @Test
    void deveNotificarProximidadeQuandoUsuarioTemFcmToken() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        mockUser.setFcmToken("token-dispositivo-1");
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(presencaAtivaRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);

        presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(DWELL_MIN + 5));

        verify(utillsNotification, times(1)).notificarUsuarioProximoGeofence(mockEvento, "token-dispositivo-1");
    }

    @Test
    void naoDeveNotificarQuandoUsuarioNaoTemFcmToken() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId); // sem fcmToken
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(presencaAtivaRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);

        presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(DWELL_MIN + 5));

        verify(utillsNotification, never()).notificarUsuarioProximoGeofence(any(), any());
    }

    @Test
    void checkinDeveConcluirMesmoSeOPushFalhar() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        mockUser.setFcmToken("token-dispositivo-1");
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(presencaAtivaRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);
        doThrow(new RuntimeException("FCM fora do ar"))
                .when(utillsNotification).notificarUsuarioProximoGeofence(any(), eq("token-dispositivo-1"));

        presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(DWELL_MIN + 5));

        // O check-in (que alimenta o heatmap) tem que ter sido gravado mesmo assim.
        verify(presencaAtivaRepository, times(1)).save(any(PresencaAtivaEntity.class));
    }

    @Test
    void naoDeveContarPresencaQuandoPermanenciaEMenorQueODwell() {
        Long userId = 1L;
        Long eventoId = 100L;

        // Passou de carro: 3 minutos dentro da cerca.
        assertThrows(IllegalStateException.class, () ->
                presencaAtivaService.processarCheckin(userId, eventoId, entradaHaMinutos(3)));

        verify(presencaAtivaRepository, never()).save(any(PresencaAtivaEntity.class));
        verify(participacaoRepository, never()).save(any(ParticipacaoEntity.class));
        verifyNoInteractions(eventoRepository);
    }

    @Test
    void deveRecusarCheckinSemHorarioDeEntrada() {
        assertThrows(IllegalArgumentException.class, () ->
                presencaAtivaService.processarCheckin(1L, 100L, null));

        verify(presencaAtivaRepository, never()).save(any(PresencaAtivaEntity.class));
    }

    @Test
    void deveRecusarHorarioDeEntradaNoFuturo() {
        long futuro = Instant.now().plusSeconds(60L * 60).toEpochMilli();

        assertThrows(IllegalArgumentException.class, () ->
                presencaAtivaService.processarCheckin(1L, 100L, futuro));

        verify(presencaAtivaRepository, never()).save(any(PresencaAtivaEntity.class));
    }

    @Test
    void deveRecusarHorarioDeEntradaAntigoDemais() {
        long ontem = Instant.now().minusSeconds(60L * 60 * 20).toEpochMilli();

        assertThrows(IllegalArgumentException.class, () ->
                presencaAtivaService.processarCheckin(1L, 100L, ontem));

        verify(presencaAtivaRepository, never()).save(any(PresencaAtivaEntity.class));
    }

    @Test
    void participacaoDeveGuardarAHoraDaEntrada_NaoADoRequest() {
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);
        EventoEntity mockEvento = eventoAtivo(eventoId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)).thenReturn(false);

        long entrada = entradaHaMinutos(40);
        presencaAtivaService.processarCheckin(userId, eventoId, entrada);

        ArgumentCaptor<ParticipacaoEntity> captor = ArgumentCaptor.forClass(ParticipacaoEntity.class);
        verify(participacaoRepository).save(captor.capture());

        LocalDateTime esperado = LocalDateTime.ofInstant(Instant.ofEpochMilli(entrada), ZoneId.systemDefault());
        assertEquals(esperado.withNano(0), captor.getValue().getCheckInAt().withNano(0));
    }
}
