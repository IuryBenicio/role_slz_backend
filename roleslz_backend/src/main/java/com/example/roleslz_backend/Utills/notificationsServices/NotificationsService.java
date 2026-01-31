package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.google.firebase.messaging.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationsService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationsService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotificationWebsocket(EventoEntity evento, EstadoEvento estado, String message) {
        //crio o Json que será enviado via websockets
        Map<String, Object> notification = new HashMap<>();
        notification.put("message", message);
        notification.put("eventoId", evento.getId());
        notification.put("estadoDoEvento", estado);

        try{
            // Enviamos para o tópico específico deste evento
            // Quem estiver no app ouvindo '/topic/evento/123' receberá isso instantaneamente
            messagingTemplate.convertAndSend("/topic/evento/" + evento.getId(), (Object) notification);
        } catch (Exception e) {
            System.err.println("Falha ao notificar via websockets" + e);
        }

    }

    public void sendNotificationFirebase(EventoEntity evento, String msg){
        List<String> tokens = evento.getConfirmacoes()
                .stream()
                .map(UserEntity::getFcmToken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if(tokens.isEmpty()) return;

        Notification notification = Notification
                .builder()
                .setTitle("Spot Rolê - " + evento.getTitle())
                .setBody(msg)
                .build();

        MulticastMessage message = MulticastMessage
                .builder()
                .setNotification(notification)
                .putData("eventoId", String.valueOf(evento.getId()))
                .addAllTokens(tokens)
                .build();

        try{
            //envia a mensagem
            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            System.out.println("Push enviado com sucesso: -> " + response.getSuccessCount());
        }catch (FirebaseMessagingException e){
            System.err.println("Erro ao enviar: -> " + e.getMessage());
        }
    }
}