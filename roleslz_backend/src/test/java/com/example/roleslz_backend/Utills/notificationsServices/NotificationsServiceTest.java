package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Utills.notificationsServices.exceptions.NotifyFail;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationsServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private NotificationsService notificationsService;

    private EventoEntity eventoMock;

    @BeforeEach
    void setUp() {
        eventoMock = new EventoEntity();
        eventoMock.setId(123L);
        eventoMock.setTitle("Balada Neon");
        eventoMock.setEstadoEvento(EstadoEvento.DURANTE);
    }

    @Test
    void deveDispararPushFCMQuandoHaTokensAlvo() throws FirebaseMessagingException {
        BatchResponse batchResponseMock = mock(BatchResponse.class);
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batchResponseMock);

        notificationsService.dispararPushFCM(
                eventoMock,
                "O rolê começou!",
                "Bora que já começou.",
                NotificationsService.TipoNotificacao.ALERTA_TEMPO,
                List.of("token-dispositivo-1", "token-dispositivo-2")
        );

        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    void naoDeveChamarFirebaseQuandoListaDeTokensEstaVazia() throws FirebaseMessagingException {
        notificationsService.dispararPushFCM(
                eventoMock,
                "Título",
                "Corpo",
                NotificationsService.TipoNotificacao.ATUALIZACAO_CRITICA,
                Collections.emptyList()
        );

        verify(firebaseMessaging, never()).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    void naoDeveChamarFirebaseQuandoListaDeTokensEhNula() throws FirebaseMessagingException {
        notificationsService.dispararPushFCM(
                eventoMock,
                "Título",
                "Corpo",
                NotificationsService.TipoNotificacao.GEOFENCING,
                null
        );

        verify(firebaseMessaging, never()).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    void deveLancarNotifyFailQuandoFirebaseFalha() throws FirebaseMessagingException {
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .thenThrow(mock(FirebaseMessagingException.class));

        assertThrows(NotifyFail.class, () -> notificationsService.dispararPushFCM(
                eventoMock,
                "O rolê começou!",
                "Bora que já começou.",
                NotificationsService.TipoNotificacao.AVALIACAO_POS_ROLE,
                List.of("token-dispositivo-1")
        ));
    }
}
