package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class UtillsNotification {

    private final NotificationsService notificationsService;

    public UtillsNotification(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    public void notificarInicioDeEvento(EventoEntity evento, String mensagem) {
        List<String> tokens = extrairTokensDaConfirmacao(evento);
        notificationsService.dispararPushFCM(evento, "Spot Rolê - " + evento.getTitle(), mensagem, NotificationsService.TipoNotificacao.ALERTA_TEMPO, tokens);
    }

    // Usado pelo EventoService quando edita (Ponto 2)
    public void notificarAtualizacaoCritica(EventoEntity evento, String mensagem) {
        List<String> tokens = extrairTokensDaConfirmacao(evento);
        notificationsService.dispararPushFCM(evento, "Atenção: Atualização no Rolê 🚨", mensagem, NotificationsService.TipoNotificacao.ATUALIZACAO_CRITICA, tokens);
    }

    // Usado pelo serviço de Rastreamento/Geofencing (Ponto 2 do Mapa)
    public void notificarUsuarioProximoGeofence(EventoEntity evento, String tokenDoUsuario) {
        notificationsService.dispararPushFCM(evento, "Rolê bombando perto de você! 🔥", evento.getTitle() + " está acontecendo agora a poucos metros daqui.", NotificationsService.TipoNotificacao.GEOFENCING, List.of(tokenDoUsuario));
    }

    // Usado pelo scheduler quando o evento é ENCERRADO: convida quem confirmou presença a avaliar o rolê.
    public void notificarAvaliacaoPosRole(EventoEntity evento) {
        List<String> tokens = extrairTokensDaConfirmacao(evento);
        notificationsService.dispararPushFCM(
                evento,
                "E aí, como foi o rolê? ⭐",
                "Conta pra galera: avalie \"" + evento.getTitle() + "\" e ajude outras pessoas a decidir.",
                NotificationsService.TipoNotificacao.AVALIACAO_POS_ROLE,
                tokens);
    }

    private List<String> extrairTokensDaConfirmacao(EventoEntity evento) {
        if (evento.getConfirmacoes() == null) {
            return List.of();
        }
        return evento.getConfirmacoes().stream()
                .map(UserEntity::getFcmToken)
                .filter(Objects::nonNull)
                .toList();
    }

}
