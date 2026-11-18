package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.notificationsServices.exceptions.NotifyFail;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NotificationsService {

    /**
     * Teto do FCM por chamada de sendEachForMulticast. Passar disso não degrada: o SDK
     * recusa a mensagem inteira, e o evento todo fica sem notificação nenhuma.
     */
    static final int LOTE_MAXIMO_FCM = 500;

    private final FirebaseMessaging firebaseMessaging;
    private final UserRepository userRepository;

    public NotificationsService(FirebaseMessaging firebaseMessaging, UserRepository userRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.userRepository = userRepository;
    }

    public enum TipoNotificacao {
        ALERTA_TEMPO,
        ATUALIZACAO_CRITICA,
        GEOFENCING,
        AVALIACAO_POS_ROLE,
        CONFIRMACAO_PRESENCA
    }

    /**
     * Entrada que recebe a entidade. Só pode ser chamada de dentro da transação,
     * porque lê campos da entidade. Prefira montar um {@link PushJob} e usar
     * {@link #disparar(PushJob)} quando o envio for acontecer depois do commit.
     */
    public void dispararPushFCM(EventoEntity evento, String titulo, String corpoMensagem,
                               TipoNotificacao tipo, List<String> tokensAlvo) {
        disparar(new PushJob(
                evento != null ? evento.getId() : null,
                evento != null && evento.getEstadoEvento() != null ? evento.getEstadoEvento().name() : "",
                titulo,
                corpoMensagem,
                tipo,
                tokensAlvo));
    }

    /**
     * Envia o push, fatiando a lista de tokens em lotes de {@link #LOTE_MAXIMO_FCM}.
     * Cada lote é independente: a falha de um não impede os outros de saírem.
     */
    public void disparar(PushJob job) {
        if (job == null || job.vazio()) {
            log.info("Nenhum token FCM válido para enviar a notificação: {}",
                    job == null ? "(job nulo)" : job.titulo());
            return;
        }

        List<String> tokens = job.tokens();
        int sucessos = 0;
        int falhas = 0;
        int lotes = 0;

        for (int inicio = 0; inicio < tokens.size(); inicio += LOTE_MAXIMO_FCM) {
            int fim = Math.min(inicio + LOTE_MAXIMO_FCM, tokens.size());
            List<String> lote = tokens.subList(inicio, fim);
            lotes++;

            BatchResponse resposta = enviarLote(job, lote);
            sucessos += resposta.getSuccessCount();
            falhas += resposta.getFailureCount();

            if (resposta.getFailureCount() > 0) {
                // ATENÇÃO: os índices da resposta são relativos AO LOTE, nunca à lista
                // completa. Passar a lista inteira aqui zeraria o token errado.
                limparTokensInvalidos(resposta, lote);
            }
        }

        log.info("Push FCM [{}] evento {}: {} token(s) em {} lote(s) | sucesso: {} | falhas: {}",
                job.tipo(), job.eventoId(), tokens.size(), lotes, sucessos, falhas);
    }

    private BatchResponse enviarLote(PushJob job, List<String> lote) {
        Notification notification = Notification.builder()
                .setTitle(job.titulo())
                .setBody(job.corpo())
                .build();

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notification)
                .putData("tipoPush", job.tipo() != null ? job.tipo().name() : "") // O Front-end vai ler isso!
                .putData("eventoId", String.valueOf(job.eventoId()))
                .putData("estadoDoEvento", job.estadoEvento() != null ? job.estadoEvento() : "")
                .addAllTokens(lote)
                .build();

        try {
            return firebaseMessaging.sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            log.error("Erro fatal ao enviar Push FCM [{}] ({} token(s) no lote)", job.tipo(), lote.size(), e);
            throw new NotifyFail("Falha ao enviar notificação push [" + job.tipo()
                    + "] para o evento " + job.eventoId() + ": " + e.getMessage());
        }
    }

    /**
     * Zera o fcmToken dos usuários cujos tokens o FCM reportou como mortos (app desinstalado /
     * token rotacionado). Sem isso o backend fica disparando pushes para tokens que nunca chegam.
     *
     * @param tokensDoLote exatamente os tokens enviados nesta chamada, na mesma ordem —
     *                     é essa ordem que dá sentido aos índices de {@code response.getResponses()}.
     */
    private void limparTokensInvalidos(BatchResponse response, List<String> tokensDoLote) {
        List<SendResponse> responses = response.getResponses();
        List<String> tokensMortos = new ArrayList<>();

        for (int i = 0; i < responses.size() && i < tokensDoLote.size(); i++) {
            SendResponse item = responses.get(i);
            if (item.isSuccessful() || item.getException() == null) {
                continue;
            }
            MessagingErrorCode code = item.getException().getMessagingErrorCode();
            if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                tokensMortos.add(tokensDoLote.get(i));
            }
        }

        if (!tokensMortos.isEmpty()) {
            userRepository.limparFcmTokens(tokensMortos);
            log.info("{} token(s) FCM inválido(s) removido(s) dos usuários.", tokensMortos.size());
        }
    }
}
