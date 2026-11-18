package com.example.roleslz_backend.Tables.events.schedullers;

import com.example.roleslz_backend.Utills.notificationsServices.PushDispatcher;
import com.example.roleslz_backend.Utills.notificationsServices.PushJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * O despertador do ciclo de vida dos eventos: roda a cada minuto e NÃO é transacional.
 *
 * <p>O trabalho de banco fica em {@link EventoCicloService}, que abre e fecha a própria
 * transação. Aqui só sobra despachar os pushes que ele devolveu — já fora da transação,
 * com a conexão do pool devolvida, e em um pool de threads separado.
 *
 * <p>É o que garante que a latência do Firebase não apareça como lentidão no banco.
 */
@Service
public class EventoSchedullers {

    private static final System.Logger LOGGER = System.getLogger(EventoSchedullers.class.getName());

    private final EventoCicloService eventoCicloService;
    private final PushDispatcher pushDispatcher;

    public EventoSchedullers(EventoCicloService eventoCicloService, PushDispatcher pushDispatcher) {
        this.eventoCicloService = eventoCicloService;
        this.pushDispatcher = pushDispatcher;
    }

    @Scheduled(fixedDelay = 60000)
    public void EventoIniciando() {
        try {
            despachar(eventoCicloService.iniciarEventos());
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Falha na rotina de início de eventos.", e);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void EventoFinalizado() {
        try {
            despachar(eventoCicloService.encerrarEventos());
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Falha na rotina de encerramento de eventos.", e);
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void EventoFinalizadoHeatMap() {
        try {
            eventoCicloService.limparHeatmap();
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "Falha na limpeza do heatmap.", e);
        }
    }

    /**
     * Entrega cada push ao executor dedicado. Um push que falhe não derruba os outros:
     * o {@link PushDispatcher} já registra e engole a própria exceção.
     */
    private void despachar(List<PushJob> pushes) {
        if (pushes == null || pushes.isEmpty()) {
            return;
        }
        for (PushJob push : pushes) {
            pushDispatcher.enviar(push);
        }
    }
}
