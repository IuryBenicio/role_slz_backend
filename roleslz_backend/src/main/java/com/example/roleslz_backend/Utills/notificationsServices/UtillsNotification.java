package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Monta as notificações do domínio e as entrega ao {@link PushDispatcher}.
 *
 * <p>Dois níveis de método aqui, de propósito:
 * <ul>
 *   <li>{@code notificarXxx(...)} — monta e já despacha. Serve para quem chama de
 *       dentro de uma transação curta de requisição HTTP.</li>
 *   <li>{@code montarXxx(...)} — só monta o {@link PushJob} e devolve. Serve para quem
 *       precisa resolver as coleções LAZY dentro da transação e despachar
 *       depois do commit, como os schedulers.</li>
 * </ul>
 * Em ambos os casos a leitura do banco (os tokens) acontece aqui, com a sessão aberta;
 * a conversa com o Firebase nunca.
 */
@Component
public class UtillsNotification {

    private final PushDispatcher pushDispatcher;

    public UtillsNotification(PushDispatcher pushDispatcher) {
        this.pushDispatcher = pushDispatcher;
    }

    // ---------- monta e despacha ----------

    public void notificarInicioDeEvento(EventoEntity evento, String mensagem) {
        pushDispatcher.enviar(montarInicioDeEvento(evento, mensagem));
    }

    // Usado pelo EventoService quando edita (Ponto 2)
    public void notificarAtualizacaoCritica(EventoEntity evento, String mensagem) {
        pushDispatcher.enviar(montar(evento,
                "Atenção: Atualização no Rolê 🚨",
                mensagem,
                NotificationsService.TipoNotificacao.ATUALIZACAO_CRITICA,
                extrairTokensDaConfirmacao(evento)));
    }

    // Usado pelo serviço de Rastreamento/Geofencing (Ponto 2 do Mapa)
    public void notificarUsuarioProximoGeofence(EventoEntity evento, String tokenDoUsuario) {
        pushDispatcher.enviar(montar(evento,
                "Rolê bombando perto de você! 🔥",
                evento.getTitle() + " está acontecendo agora a poucos metros daqui.",
                NotificationsService.TipoNotificacao.GEOFENCING,
                List.of(tokenDoUsuario)));
    }

    // Scheduler, evento ENCERRADO: convida quem ESTEVE no evento (participacao_evento) a avaliar.
    public void notificarAvaliacaoPosRole(EventoEntity evento, Collection<UserEntity> destinatarios) {
        pushDispatcher.enviar(montarAvaliacaoPosRole(evento, destinatarios));
    }

    // Scheduler, evento ENCERRADO: pergunta a quem demonstrou interesse mas não teve
    // presença detectada se foi ao evento (fallback para o geofence que não disparou).
    // Enquadrado no EVENTO, nunca no rastreamento.
    public void perguntarPresencaNoEvento(EventoEntity evento, Collection<UserEntity> destinatarios) {
        pushDispatcher.enviar(montarPerguntaDePresenca(evento, destinatarios));
    }

    // ---------- só monta (para despachar após o commit) ----------

    public PushJob montarInicioDeEvento(EventoEntity evento, String mensagem) {
        return montar(evento,
                "Spot Rolê - " + evento.getTitle(),
                mensagem,
                NotificationsService.TipoNotificacao.ALERTA_TEMPO,
                extrairTokensDaConfirmacao(evento));
    }

    public PushJob montarAvaliacaoPosRole(EventoEntity evento, Collection<UserEntity> destinatarios) {
        return montar(evento,
                "E aí, como foi o rolê? ⭐",
                "Conta pra galera: avalie \"" + evento.getTitle() + "\" e ajude outras pessoas a decidir.",
                NotificationsService.TipoNotificacao.AVALIACAO_POS_ROLE,
                extrairTokens(destinatarios));
    }

    public PushJob montarPerguntaDePresenca(EventoEntity evento, Collection<UserEntity> destinatarios) {
        return montar(evento,
                "Rolou o " + evento.getTitle() + " 🎉",
                "Você foi? Marque presença e conte como foi.",
                NotificationsService.TipoNotificacao.CONFIRMACAO_PRESENCA,
                extrairTokens(destinatarios));
    }

    // ---------- interno ----------

    private PushJob montar(EventoEntity evento, String titulo, String corpo,
                           NotificationsService.TipoNotificacao tipo, List<String> tokens) {
        return new PushJob(
                evento.getId(),
                evento.getEstadoEvento() != null ? evento.getEstadoEvento().name() : "",
                titulo,
                corpo,
                tipo,
                tokens);
    }

    private List<String> extrairTokens(Collection<UserEntity> usuarios) {
        if (usuarios == null) {
            return List.of();
        }
        return usuarios.stream()
                .map(UserEntity::getFcmToken)
                .filter(Objects::nonNull)
                .toList();
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
