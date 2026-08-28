package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.categoria.exceptions.CategoryNotFounded;
import com.example.roleslz_backend.Tables.categoria.repository.CategoriaRepository;
import com.example.roleslz_backend.Tables.events.DTO.*;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventPrivacity;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.PhotosEventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.*;
import com.example.roleslz_backend.Tables.events.mapper.EventoMapperClass;
import com.example.roleslz_backend.Tables.events.mapper.PhotoEventoMapper;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.repository.PhotosEventoRepository;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import com.example.roleslz_backend.Utills.notificationsServices.NotificationsService;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.grammars.hql.HqlParser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final PhotosEventoRepository photosEventoRepository;
    private final PhotoEventoMapper photoEventoMapper;
    private final EventoMapperClass eventoMapperClass;
    private final EventoMapperClass eventoWithDistanciaMapper;
    private final UserDetailsMapperClass userDetailsMapper;
    private final CategoriaRepository categoriaRepository;
    private final UserRepository userRepository;
    private final UserUtils userUtils;
    private final UtillsNotification utillsNotification;
    private final ParticipacaoRepository participacaoRepository;

    public EventoService(EventoRepository eventoRepository, PhotosEventoRepository photosEventoRepository, PhotoEventoMapper photoEventoMapper, EventoMapperClass eventoMapperClass, EventoMapperClass eventoWithDistanciaMapper, UserDetailsMapperClass userDetailsMapper, CategoriaRepository categoriaRepository, UserRepository userRepository, UserUtils userUtils, UtillsNotification utillsNotification, ParticipacaoRepository participacaoRepository) {
        this.eventoRepository = eventoRepository;
        this.photosEventoRepository = photosEventoRepository;
        this.photoEventoMapper = photoEventoMapper;
        this.eventoMapperClass = eventoMapperClass;
        this.eventoWithDistanciaMapper = eventoWithDistanciaMapper;
        this.userDetailsMapper = userDetailsMapper;
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
        this.userUtils = userUtils;
        this.utillsNotification = utillsNotification;
        this.participacaoRepository = participacaoRepository;
    }

    private static final String msgEventNotFounded = "Evento não encontrado!";

    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    //Services de localização

    @Transactional
    public Set<EventoDTOResponseDistanceClass> getEventosInMapArea(double minLat, double minLon, double maxLat, double maxLon){
        try{
            double centerLng = (minLon + maxLon) / 2.0;
            double centerLat = (minLat + maxLat) / 2.0;

            List<EventoComDistanciaProjection> eventos = eventoRepository.buscarEventosEmUmaArea(
                    centerLng, centerLat, minLon, minLat, maxLon, maxLat
            );

            return eventos.stream()
                    .map(eventoMapperClass::toDTOResponseDistance).collect(Collectors.toSet());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorOnReturn("Tivemos problemas para pegar eventos na area " + e.getMessage());
        }
    }

    //Services
    @Transactional
    public EventDTOClass getEventoById(long id){
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded(msgEventNotFounded));
        long userLogadoId = userUtils.getUserLogado().getId();

        EventDTOClass dto = eventoMapperClass.toDTO(evento);

        if (!evento.getExibirConfirmacoesPublicamente() && evento.getOrganizador().getId() != userLogadoId) {
            dto.setConfirmacoes(new HashSet<>());
        }

        // O usuário logado já teve presença confirmada neste evento? (badge "estive presente" + pode avaliar)
        dto.setParticipacaoConfirmada(participacaoRepository.existsByUserIdAndEventoId(userLogadoId, id));

        return dto;
    }

    public List<EventDTOSumary> buscarProximos(Double lat, Double lng, Double raioKm) {
        Double raioEmMetros = raioKm * 1000;

        List<EventoEntity> eventos = eventoRepository.findEventosProximosGeoFence(lat, lng, raioEmMetros);

        // Mapeia para um DTO leve (apenas o necessário para o Fence e identificação)
        return eventos.stream().map(evento -> {
            EventDTOSumary dto = new EventDTOSumary();
            dto.setId(evento.getId());
            dto.setTitle(evento.getTitle());
            dto.setLatitude(evento.getSpot().getLocalizacao().getY());
            dto.setLongitude(evento.getSpot().getLocalizacao().getX());
            return dto;
        }).toList();
    }

    @Transactional
    public EventDTOClass getEventoByTitle(String title){
        EventoEntity evento = eventoRepository.findByTitle(title).orElseThrow(()->new EventNotFounded(msgEventNotFounded));
        try{
            return eventoMapperClass.toDTO(evento);
        } catch (Exception e) {
            throw new EventNotFounded("Evento não retornado");
        }
    }

    @Transactional
    public Set<EventoDTOWithIDClass> getEventsPagination(int pagina, int itens){
        Set<EventoEntity> eventos = eventoRepository.findAll(PageRequest.of(pagina, itens)).toSet();
        return eventos.stream().map(eventoMapperClass::toDTOWithID).collect(Collectors.toSet());
    }

    public void changePrivacity(long id){
        EventoEntity evento = eventoRepository.findByIdAndOrganizadorId(id, userUtils.getUserLogado().getId()).orElseThrow(()-> new EventNotFounded("Nenhum evento foi encontrado pertencendo ao usuário logado!"));

        EventPrivacity novaPrivacidade = (evento.getEventPrivacity() == EventPrivacity.PRIVADO)
                ? EventPrivacity.PUBLICO
                : EventPrivacity.PRIVADO;

        evento.setEventPrivacity(novaPrivacidade);

        eventoRepository.save(evento);
    }

    public Set<EventoDTOWithIDClass> getEventoByOwnerId(long id){
        UserEntity user = userRepository.findById(id).orElseThrow(()-> new UserNotFounded("Usuário não encontrado!"));

        if(user.getEventos().isEmpty()){
            throw new EventNotFounded("Eventos não encontrados");
        }

        return user.getEventos().stream().map(eventoMapperClass::toDTOWithID).collect(Collectors.toSet());
    }


    @Transactional
    public void addCategoria(long id, String categoria){
        EventoEntity eventoEntity = eventoRepository.findByIdAndOrganizadorId(id, userUtils.getUserLogado().getId()).orElseThrow(()->new EventNotFounded(msgEventNotFounded));
        CategoriaEntity categoriaEntity = categoriaRepository.findByName(categoria).orElseThrow(()-> new CategoryNotFounded("Categoria não encontrada"));

        eventoEntity.getCategorias().add(categoriaEntity);

        eventoRepository.save(eventoEntity);

    }

    @Transactional
    public EventDTOClass createEvento(EventDTORequestClass eventoDTO){
        Optional<EventoEntity> exists = eventoRepository.findByTitle(eventoDTO.getTitle());

        if(exists.isPresent()){
            throw new EventExists("Evento já existe");
        }

        if(eventoDTO.getEstadoEvento().equals(EstadoEvento.ENCERRADO)){
            throw new EventExists("Evento com estado incorreto");
        }

        Point localizacao = factory.createPoint(new Coordinate(eventoDTO.getLongitude(), eventoDTO.getLatitude()));

        EventoEntity novoEvento = eventoMapperClass.toEntity(eventoDTO);

        UserEntity owner = userRepository.findByEmail(eventoDTO.getOrganizador().email()).orElseThrow(()-> new UserNotFounded("Organizador não encontrado!"));

        SpotEntity spot = new SpotEntity();

        spot.setLocalizacao(localizacao);

        novoEvento.setSpot(spot);
        novoEvento.setOrganizador(owner);

        // Lógica do placeholder
        if (novoEvento.getImages() == null || novoEvento.getImages().isEmpty()) {
            PhotosEventoEntity placeholder = new PhotosEventoEntity();
            placeholder.setLink("https://placehold.co/600x400");
            placeholder.setEvento(novoEvento);

            List<PhotosEventoEntity> images = new ArrayList<>();
            images.add(placeholder);
            novoEvento.setImages(images);
        }

        EventoEntity evento = eventoRepository.save(novoEvento);

        return eventoMapperClass.toDTO(evento);
    }

    @Transactional
    public void editPrice(long id, NewPriceDTO price){
        EventoEntity evento = eventoRepository.findByIdAndOrganizadorId(id, userUtils.getUserLogado().getId()).orElseThrow(()->new EventNotFounded(msgEventNotFounded));

        String msg = "O evento " + evento.getTitle() + " ALTEROU SEU PREÇO, entre no app e confira!";
        try {
            utillsNotification.notificarAtualizacaoCritica(evento, msg);
        } catch (Exception e) {
            log.error("Falha ao notificar alteração de preço do evento ID: {}", evento.getId(), e);
        }

        BigDecimal zero = BigDecimal.ZERO;

        if(price.newPrice().compareTo(zero) < 0){
            throw new InvalidPrice("Preço inválido");
        }

        evento.setPrice(price.newPrice());
        eventoRepository.save(evento);

    }

    @Transactional
    public EventDTOClass editEvento(EventDTORequestClass eventoDTO){
        if (eventoDTO.getId() == null) {
            throw new EventNotFounded("Id do evento não informado");
        }
        EventoEntity evento = eventoRepository.findByIdAndOrganizadorId(eventoDTO.getId(), userUtils.getUserLogado().getId()).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        String msg = "O evento " + evento.getTitle() + " teve alterações importantes de horário ou local. Confira no app!";
        try {
            utillsNotification.notificarAtualizacaoCritica(evento, msg);
        } catch (Exception e) {
            log.error("Falha ao notificar edição do evento ID: {}", evento.getId(), e);
        }

        eventoMapperClass.updateEntityFromDto(eventoDTO, evento);

        EventoEntity salvo = eventoRepository.save(evento);

        return eventoMapperClass.toDTO(salvo);
    }

    @Transactional
    public void deleteEvento(String name){
        EventoEntity evento = eventoRepository.findByOrganizadorIdAndTitle(userUtils.getUserLogado().getId(), name).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        LocalDateTime dataHoraAtual = LocalDateTime.now();

        if(evento.getStartDate().isBefore(dataHoraAtual)){
            String msg = "O evento " + evento.getTitle() + " foi deletado 😞";
            try {
                utillsNotification.notificarAtualizacaoCritica(evento, msg);
            } catch (Exception e) {
                log.error("Falha ao notificar exclusão do evento ID: {}", evento.getId(), e);
            }
        }

        eventoRepository.delete(evento);
    }

    @Transactional
    public Set<UserDTODetails> getConfirms (long eventoId){
        EventoEntity evento = eventoRepository.findById(eventoId).orElseThrow(()-> new EventNotFounded(msgEventNotFounded));

        try{
            return evento.getConfirmacoes().stream().map(userDetailsMapper::toDTO).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new ErrorOnReturn("Confirmações não retornadas " + e.getMessage());
        }
    }

    public PaginationWraper getEventsNowPagination(int pagina, int itens) {
        LocalDateTime agora = LocalDateTime.now();
        Page<EventoEntity> pageResult = eventoRepository.findByStartDateBetween(
                agora.minusHours(3), agora.plusHours(6), PageRequest.of(pagina, itens)
        );

        return new PaginationWraper(
                pageResult.getContent().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                pageResult.getNumber(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    public PaginationWraper getMinhaAgendaPaginada(int pagina, int itens){
        Page<EventoEntity> pageResult = eventoRepository.findMinhaAgenda(userUtils.getUserLogado().getId(), PageRequest.of(pagina, itens));

        PaginationWraper response = new PaginationWraper(
                pageResult.getContent().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                pageResult.getNumber(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );

        return response;
    };

    public PaginationWraper getEventsFuturePagination(int pagina, int itens, String filtro) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime fim = switch (filtro.toUpperCase()) {
            case "HOJE" -> agora.toLocalDate().atTime(23, 59, 59);
            case "SEMANA" -> agora.plusDays(7);
            case "MES" -> agora.plusMonths(1);
            default -> agora.plusDays(30);
        };

        // 2. Busca paginada de eventos futuros
        Page<EventoEntity> pageResult = eventoRepository.findByStartDateBetween(agora, fim, PageRequest.of(pagina, itens));

        return new PaginationWraper(
                pageResult.getContent().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                pageResult.getNumber(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Transactional
    public void removeConfirms (long event_id){
        EventoEntity evento = eventoRepository
                .findById(event_id)
                .orElseThrow(()-> new EventNotFounded(msgEventNotFounded));

        UserEntity confirm = evento
                .getConfirmacoes()
                .stream()
                .filter(a -> a.getId() == userUtils
                        .getUserLogado()
                        .getId())
                        .findFirst()
                        .orElseThrow(()->new EventNotFounded("Confirmação não encontrada"));

        try{
            evento.getConfirmacoes().remove(confirm);
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new EventoNotDeleted("Confirmação não deletada");
        }
    }
}
