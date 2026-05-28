package com.example.roleslz_backend.Tables.events.schedullers;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotEdited;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Utills.notificationsServices.NotificationsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoSchedullers {

    private final EventoRepository eventoRepository;
    private final NotificationsService notificationsService;


    public EventoSchedullers(EventoRepository eventoRepository, NotificationsService notificationsService) {
        this.eventoRepository = eventoRepository;
        this.notificationsService = notificationsService;
    }


    private String msg5pre = "Seu evento começou!";
    private String msg30pre = "Seu evento irá começar em 30 minutos!";

    @Scheduled(fixedDelay = 60000)
    public void EventoIniciando(){

        LocalDateTime agora = LocalDateTime.now();

        List<EventoEntity> eventos5pos = eventoRepository
                .findByEstadoEventoAndStartDateBefore(EstadoEvento.PRE,agora.plusMinutes(5))
                .orElse(List.of());
        for (EventoEntity evento: eventos5pos){
            //Envia notificação via websockets
            notificationsService.sendNotificationWebsocket(evento, EstadoEvento.DURANTE, msg5pre);

            //Envia notificação via firebase
            notificationsService.sendNotificationFirebase(evento, msg5pre);

            //muda estado do evento
            try{
                evento.setEstadoEvento(EstadoEvento.DURANTE);
                eventoRepository.save(evento);
            } catch (Exception e) {
                throw new EventNotEdited("Estado do evento não alterado!");
            }
        }

        List<EventoEntity> eventos30pos = eventoRepository
                .findByEstadoEventoAndStartDateBefore(EstadoEvento.PRE,agora.plusMinutes(30))
                .orElse(List.of());

        for (EventoEntity evento: eventos30pos){
            //Envia notificação via websockets
            notificationsService.sendNotificationWebsocket(evento, EstadoEvento.DURANTE, msg30pre);

            //Envia notificação via firebase
            notificationsService.sendNotificationFirebase(evento, msg30pre);
        }

    }

    @Scheduled(fixedDelay = 60000)
    public void EventoFinalizado(){
        LocalDateTime agora = LocalDateTime.now();

        List<EventoEntity> evento5pos = eventoRepository
                .findByEstadoEventoAndEndDateBefore(EstadoEvento.DURANTE, agora.plusMinutes(5))
                .orElse(List.of());

        for(EventoEntity evento: evento5pos){
         try{
            evento.setEstadoEvento(EstadoEvento.ENCERRADO);
            eventoRepository.save(evento);
         } catch (Exception e) {
             throw new EventNotEdited("Estado do evento não alterado!");
         }
        }
    }

}
