package com.example.roleslz_backend.Tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.*;
import com.example.roleslz_backend.Tables.events.services.EventoService;
import com.example.roleslz_backend.Tables.events.services.PresencaAtivaService;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/evento")
public class EventoController {

    private final EventoService eventoService;
    private final PresencaAtivaService presencaAtivaService;

    public EventoController(EventoService eventoService, PresencaAtivaService presencaAtivaService) {
        this.eventoService = eventoService;
        this.presencaAtivaService = presencaAtivaService;
    }

    @PostMapping("create")
    public ResponseEntity<EventDTOClass> createEvento(@RequestBody EventDTORequestClass eventoDTO){
        EventDTOClass evento = eventoService.createEvento(eventoDTO);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("get_many")
    public ResponseEntity<PaginationWraper> getEventsPagination(
            @RequestParam("pagina") int pagina,
            @RequestParam("itens") int itens,
            @RequestParam String filtro){
        PaginationWraper response = eventoService.getEventsFuturePagination(pagina, itens, filtro);
        System.out.println("TESTE: " + response.content().stream().map(a-> a.toString()));
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("get_many_now")
    public ResponseEntity<PaginationWraper> getEventsNowPagination(
            @RequestParam("pagina") int pagina,
            @RequestParam("itens") int itens){
        PaginationWraper response = eventoService.getEventsNowPagination(pagina, itens);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("changing_event_privacity/{id}")
    public ResponseEntity<String> changingEventPrivacity(@PathVariable("id") long id){
        eventoService.changePrivacity(id);
        return ResponseEntity.ok("Privacidade do evento alterada");
    }

    @PostMapping("add_category")
    public ResponseEntity<String> addCategoria(@RequestBody CategoryAddRequestDTO dto){
        eventoService.addCategoria(dto.eventoId(), dto.categoriaName());
        return ResponseEntity.status(202).body("Categoria adicionada");
    }

    @GetMapping("proximos")
    public ResponseEntity<List<EventDTOSumary>> getEventosProximosGeoFence(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        // buscarProximos recebe o raio em QUILÔMETROS e multiplica por 1000 internamente.
        // Passar 5000.0 aqui virava um raio de 5.000 km — praticamente o país inteiro,
        // o oposto da janela deslizante de geofences que o app espera.
        return ResponseEntity.ok(eventoService.buscarProximos(lat, lng, 5.0));
    }

    @GetMapping("minha_agenda")
    public ResponseEntity<PaginationWraper> getMinhaAgenda(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int itens) {

        PaginationWraper response = eventoService.getMinhaAgendaPaginada(pagina, itens);
        return ResponseEntity.ok(response);
    }

    @PostMapping("get_events_in_map_area")
    public ResponseEntity<Set<EventoDTOResponseDistanceClass>> getEventsInMapArea(@RequestBody GetsEventsInMapAreaDTO dto){
       Set<EventoDTOResponseDistanceClass> eventos = eventoService.getEventosInMapArea(
                dto.minLat(), dto.minLon(), dto.maxLat(), dto.maxLon()
        );        return ResponseEntity.ok(eventos);
    }

    @GetMapping("heatmap")
    public ResponseEntity<List<HeatPointDTO>> getHeatmap(
            @RequestParam Double minLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLat,
            @RequestParam Double maxLon) {
        List<HeatPointDTO> pontos = presencaAtivaService.getHeatmapData(minLat, minLon, maxLat, maxLon);
        return ResponseEntity.ok(pontos);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<EventDTOClass> getEventById(@PathVariable long id){
        EventDTOClass evento = eventoService.getEventoById(id);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("get_by_title")
    public ResponseEntity<EventDTOClass> getEventByTitle(@RequestParam String title){
        EventDTOClass evento = eventoService.getEventoByTitle(title);
        return ResponseEntity.ok(evento);
    }

    @GetMapping("get_by_owner/{id}")
    public ResponseEntity<Set<EventoDTOWithIDClass>> getEventByOwner(@PathVariable long id){
        Set<EventoDTOWithIDClass> eventos = eventoService.getEventoByOwnerId(id);
        return ResponseEntity.ok(eventos);
    }


    @PatchMapping("edit")
    public ResponseEntity<EventDTOClass> editEvent(@RequestBody EventDTORequestClass eventoDTO){
        EventDTOClass evento = eventoService.editEvento(eventoDTO);
        return ResponseEntity.ok(evento);
    }

    @PatchMapping("edit_price/{id}")
    public ResponseEntity<NewPriceDTO> editPrice(@PathVariable long id, @RequestBody NewPriceDTO newPriceDTO){
        eventoService.editPrice(id, newPriceDTO);
        return ResponseEntity.ok(newPriceDTO);
    }

    @GetMapping("get_confirms/{id}")
    public ResponseEntity<Set<UserDTODetails>> getConfirms(@PathVariable long id){
        Set<UserDTODetails> confirms = eventoService.getConfirms(id);
        return ResponseEntity.ok(confirms);
    }

    @PatchMapping("remove_confirm/{event_id}")
    public ResponseEntity<String> removeConfirm(@PathVariable long event_id){
        eventoService.removeConfirms(event_id);
        return ResponseEntity.status(202).body("Confirmação removida");
    }

    // Era delete/{name}: apagar por título quebra com nomes repetidos.
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id){
        eventoService.deleteEvento(id);
        return ResponseEntity.status(HttpStatus.OK).body("Evento deletado");
    }
}