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
import com.example.roleslz_backend.Tables.events.repository.PhotosEventoRepository;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.mapper.UserDetailsMapperClass;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Utills.UploadImage.StorageFile;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final StorageFile storageFile;

    public EventoService(EventoRepository eventoRepository, PhotosEventoRepository photosEventoRepository, PhotoEventoMapper photoEventoMapper, EventoMapperClass eventoMapperClass, EventoMapperClass eventoWithDistanciaMapper, UserDetailsMapperClass userDetailsMapper, CategoriaRepository categoriaRepository, UserRepository userRepository, StorageFile storageFile) {
        this.eventoRepository = eventoRepository;
        this.photosEventoRepository = photosEventoRepository;
        this.photoEventoMapper = photoEventoMapper;
        this.eventoMapperClass = eventoMapperClass;
        this.eventoWithDistanciaMapper = eventoWithDistanciaMapper;
        this.userDetailsMapper = userDetailsMapper;
        this.categoriaRepository = categoriaRepository;
        this.userRepository = userRepository;
        this.storageFile = storageFile;
    }


    private static final String msgEventNotFounded = "Evento não encontrado!";

    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    //Services de localização

    @Transactional
    public Set<EventoDTOResponseDistanceClass> getEventosInMapArea(double minLat, double minLon, double maxLat, double maxLon){
        try{
            Point centroMapa = factory.createPoint(new Coordinate((minLon + maxLon)/2, (minLat + maxLat)/2));

            List<EventoComDistanciaProjection> eventos = eventoRepository.buscarEventosProximos(
                    centroMapa, minLon, minLat, maxLon, maxLat
            );

            return eventos.stream()
                    .map(eventoMapperClass::toDTOResponseDistance).collect(Collectors.toSet());
        } catch (Exception e) {
            throw new ErrorOnReturn("Tivemos problemas para pegar eventos na area " + e);
        }
    }

    //Services
    @Transactional
    public EventDTOClass getEventoById(long id){
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded(msgEventNotFounded));
        try{
            return eventoMapperClass.toDTO(evento);
        } catch (Exception e) {
            throw new EventNotFounded("Evento não retornado");
        }
    }

    @Transactional
    public List<PhotosEventoDTORes> uploadFotos(List<MultipartFile> fotos, long id){

        List<String> urls = storageFile.uploadMultipleFiles(fotos);

        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded(msgEventNotFounded));

        List<PhotosEventoEntity> ListaFotos = new ArrayList<>();

        for (String url: urls){
            PhotosEventoEntity photosEventoEntity = new PhotosEventoEntity();
            photosEventoEntity.setLink(url);
            photosEventoEntity.setEvento(evento);
            ListaFotos.add(photosEventoEntity);
        }

        try{
            photosEventoRepository.saveAll(ListaFotos);
            return ListaFotos.stream().map(photoEventoMapper::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorInAddImages("Erro ao adicionar imagem");
        }
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
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded(msgEventNotFounded));

        try {
            if (evento.getEventPrivacity() == EventPrivacity.PRIVADO) {
                evento.setEventPrivacity(EventPrivacity.PUBLICO);
            } else {
                evento.setEventPrivacity(EventPrivacity.PRIVADO);
            }
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new EventNotEdited("Privacidade do evento não alterada!");
        }
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
        EventoEntity eventoEntity = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded(msgEventNotFounded));
        CategoriaEntity categoriaEntity = categoriaRepository.findByName(categoria).orElseThrow(()-> new CategoryNotFounded("Categoria não encontrada"));

        eventoEntity.getCategorias().add(categoriaEntity);

        try{
            eventoRepository.save(eventoEntity);
        } catch (Exception e) {
            throw new CategoryNotAddedInEvent("Categoria não adicionada no evento");
        }
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
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()->new EventNotFounded(msgEventNotFounded));

        BigDecimal zero = BigDecimal.ZERO;

        if(price.newPrice().compareTo(zero) < 0){
            throw new InvalidPrice("Preço inválido");
        }

        try{
            evento.setPrice(price.newPrice());
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new EventNotEdited("Evento não editado");
        }

    }

    @Transactional
    public EventDTOClass editEvento(long id,EventDTORequestClass eventoDTO){
       EventoEntity evento = eventoRepository.findById(id).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

       eventoMapperClass.updateEntityFromDto(eventoDTO, evento);

       EventoEntity salvo = eventoRepository.save(evento);

       return eventoMapperClass.toDTO(salvo);
    }

    @Transactional
    public void deleteEvento(long id){
        EventoEntity evento = eventoRepository.findById(id).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        try{
            eventoRepository.delete(evento);
        } catch (Exception e) {
            throw new EventoNotDeleted("Evento não deletado");
        }
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

    @Transactional
    public void removeConfirms (long event_id, long user_id){
        EventoEntity evento = eventoRepository.findById(event_id).orElseThrow(()-> new EventNotFounded(msgEventNotFounded));

        UserEntity confirm = evento.getConfirmacoes().stream().filter(a -> a.getId() == user_id).findFirst().orElseThrow(()->new EventNotFounded("Confirmação não encontrada"));

        try{
            evento.getConfirmacoes().remove(confirm);
            eventoRepository.save(evento);
        } catch (Exception e) {
            throw new EventoNotDeleted("Confirmação não deletada");
        }
    }
}
