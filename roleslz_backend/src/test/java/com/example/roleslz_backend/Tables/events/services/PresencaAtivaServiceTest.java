package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.PresencaAtivaEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaAtivaServiceTest {

    @Mock
    private PresencaAtivaRepository presencaAtivaRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private PresencaAtivaService presencaAtivaService;

    @Test
    void deveProcessarCheckinComSucessoQuandoUsuarioEEventoExistem() {
        // Arrange (Preparação)
        Long userId = 1L;
        Long eventoId = 100L;

        UserEntity mockUser = new UserEntity();
        mockUser.setId(userId);

        EventoEntity mockEvento = new EventoEntity();
        mockEvento.setId(eventoId);

        // 🔥 A CORREÇÃO ESTÁ AQUI: Dizemos ao teste que o evento está ativo agora!
        mockEvento.setEstadoEvento(EstadoEvento.DURANTE); // Ou PRE, dependendo da sua regra
        mockEvento.setStartDate(java.time.LocalDateTime.now().minusHours(1));
        mockEvento.setEndDate(java.time.LocalDateTime.now().plusHours(2));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(mockEvento));
        when(presencaAtivaRepository.existsByUserIdAndEventoId(mockUser.getId(), mockEvento.getId())).thenReturn(false);

        // Act (Ação)
        presencaAtivaService.processarCheckin(userId, eventoId);

        // Assert (Verificação)
        verify(presencaAtivaRepository, times(1)).save(any(PresencaAtivaEntity.class));
    }
}
