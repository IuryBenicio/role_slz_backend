package com.example.roleslz_backend.Utills.notificationsServices;

import com.example.roleslz_backend.Utills.configurations.AsyncConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Entrega o push fora da thread de quem pediu.
 *
 * <p>Precisa ser um bean separado do {@link NotificationsService}: o @Async do Spring
 * funciona por proxy, e proxy não intercepta chamada que um objeto faz a si mesmo.
 * Se o método assíncrono morasse no próprio NotificationsService e fosse chamado
 * internamente, rodaria síncrono sem avisar ninguém.
 */
@Slf4j
@Component
public class PushDispatcher {

    private final NotificationsService notificationsService;

    public PushDispatcher(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @Async(AsyncConfiguration.PUSH_EXECUTOR)
    public void enviar(PushJob job) {
        if (job == null || job.vazio()) {
            return;
        }
        try {
            notificationsService.disparar(job);
        } catch (Exception e) {
            // Exceção em método @Async void não tem para onde subir: o chamador já
            // seguiu em frente. Então ela morre aqui, registrada.
            log.error("Push do evento {} falhou em background (tipo {}).", job.eventoId(), job.tipo(), e);
        }
    }
}
