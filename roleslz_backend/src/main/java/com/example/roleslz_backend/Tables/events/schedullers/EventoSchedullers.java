package com.example.roleslz_backend.Tables.events.schedullers;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventoSchedullers {

    private final EventoRepository eventoRepository;
    private final UtillsNotification utillsNotification;
    private final PresencaAtivaRepository presencaAtivaRepository;

    // Loggers
    private static final System.Logger LOGGER = System.getLogger(EventoSchedullers.class.getName());

    public EventoSchedullers(EventoRepository eventoRepository, UtillsNotification utillsNotification, PresencaAtivaRepository presencaAtivaRepository) {
        this.eventoRepository = eventoRepository;
        this.utillsNotification = utillsNotification;
        this.presencaAtivaRepository = presencaAtivaRepository;
    }

    private final String msg5pre = "Seu evento começou!";
    private final String msg30pre = "Seu evento irá começar em 30 minutos!";

    @Scheduled(fixedDelay = 60000)
    @Transactional // Adicionado para garantir a transação do Lote
    public void EventoIniciando() {
        LocalDateTime agora = LocalDateTime.now();

        // 1. Notificação de 30 Minutos (Busca eventos que começam exatamente entre 29 e 30 minutos a partir de agora)
        List<EventoEntity> eventos30pos = eventoRepository
                .findByEstadoEventoAndStartDateBetween(EstadoEvento.PRE, agora.plusMinutes(29), agora.plusMinutes(30))
                .orElse(List.of());

        for (EventoEntity evento : eventos30pos) {
            try {
                utillsNotification.notificarInicioDeEvento(evento, msg30pre);
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao notificar (30min) evento ID: " + evento.getId(), e);
            }
        }

        // 2. Eventos Começando Agora/Daqui a 5 min (Muda o estado para DURANTE)
        // OBS: Usamos 'Before' aqui pois tudo que atrasou e ainda é PRE, DEVE virar DURANTE
        List<EventoEntity> eventos5pos = eventoRepository
                .findByEstadoEventoAndStartDateBefore(EstadoEvento.PRE, agora.plusMinutes(5))
                .orElse(List.of());

        List<EventoEntity> eventosParaSalvar = new ArrayList<>();

        for (EventoEntity evento : eventos5pos) {
            // Notificação é um efeito colateral best-effort: sua falha NÃO pode impedir
            // a transição de estado do evento (senão ele ficaria preso em PRE para sempre).
            try {
                utillsNotification.notificarInicioDeEvento(evento, msg5pre);
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao notificar (início) evento ID: " + evento.getId(), e);
            }

            try {
                evento.setEstadoEvento(EstadoEvento.DURANTE);
                eventosParaSalvar.add(evento); // Prepara para salvar todos juntos depois
            } catch (Exception e) {
                // Ao invés de quebrar a rotina inteira, logamos o erro e o loop continua!
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao iniciar evento ID: " + evento.getId(), e);
            }
        }

        // Salva todos de uma vez (Melhor performance)
        if (!eventosParaSalvar.isEmpty()) {
            eventoRepository.saveAll(eventosParaSalvar);
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void EventoFinalizado() {
        LocalDateTime agora = LocalDateTime.now();

        // Alterado de plusMinutes(5) para 'agora'. Se o evento termina às 10h, ele encerra às 10h.
        List<EventoEntity> eventosParaEncerrar = eventoRepository
                .findByEstadoEventoAndEndDateBeforeAndPresencaLimpaFalse(EstadoEvento.DURANTE, agora)
                .orElse(List.of());

        List<EventoEntity> eventosParaSalvar = new ArrayList<>();

        for (EventoEntity evento : eventosParaEncerrar) {
            try {
                evento.setEstadoEvento(EstadoEvento.ENCERRADO);
                eventosParaSalvar.add(evento);
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao encerrar evento ID: " + evento.getId(), e);
            }
        }

        if (!eventosParaSalvar.isEmpty()) {
            eventoRepository.saveAll(eventosParaSalvar);
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void EventoFinalizadoHeatMap() {
        LocalDateTime agora = LocalDateTime.now();

        // Agora busca APENAS os eventos que ainda não foram limpos (Evita Memory Leak)
        List<EventoEntity> eventosParaLimpar = eventoRepository
                .findByEstadoEventoAndEndDateBeforeAndPresencaLimpaFalse(EstadoEvento.ENCERRADO, agora)
                .orElse(List.of());

        List<EventoEntity> eventosSalvar = new ArrayList<>();

        for (EventoEntity evento : eventosParaLimpar) {
            try {
                presencaAtivaRepository.deleteByEventoId(evento.getId());
                evento.setPresencaLimpa(true);
                eventosSalvar.add(evento);
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao limpar heatmap do evento ID: " + evento.getId(), e);
            }
        }

        if (!eventosSalvar.isEmpty()) {
            eventoRepository.saveAll(eventosSalvar);
        }
    }
}