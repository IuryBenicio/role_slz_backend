package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Utills.notificationsServices.exceptions.NotifyFail;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class NotificationsService {

    private final FirebaseMessaging firebaseMessaging;

    public NotificationsService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public enum TipoNotificacao {
        ALERTA_TEMPO,
        ATUALIZACAO_CRITICA,
        GEOFENCING,
        AVALIACAO_POS_ROLE
    }

    public void dispararPushFCM(EventoEntity evento, String titulo, String corpoMensagem, TipoNotificacao tipo, List<String> tokensAlvo) {

        if (tokensAlvo == null || tokensAlvo.isEmpty()) {
            log.info("Nenhum token FCM válido para enviar a notificação: {}", titulo);
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(titulo)
                .setBody(corpoMensagem)
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("tipoPush", tipo.name()) // O Front-end vai ler isso!
                .putData("eventoId", String.valueOf(evento.getId()))
                .putData("estadoDoEvento", evento.getEstadoEvento() != null ? evento.getEstadoEvento().name() : "")
                .addAllTokens(tokensAlvo)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            log.info("Push FCM [{}] disparado! Sucesso: {} | Falhas: {}", tipo.name(), response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("Erro fatal ao enviar Push FCM [{}]", tipo.name(), e);
            throw new NotifyFail("Falha ao enviar notificação push [" + tipo.name() + "] para o evento " + evento.getId() + ": " + e.getMessage());
        }
    }
}