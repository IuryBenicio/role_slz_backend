package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTORequest;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTOResponseDistance;
import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-27T15:40:10-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class EventoWithDistanciaMapperImpl implements EventoWithDistanciaMapper {

    @Override
    public EventoDTOResponseDistance toDTO(EventoComDistanciaProjection entity) {
        if ( entity == null ) {
            return null;
        }

        double distanciaMetros = 0.0d;
        long id = 0L;
        String title = null;
        String description = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        String enderecoExtenso = null;
        UserEntity organizador = null;
        String imageUrl = null;
        EstadoEvento estadoEvento = null;
        BigDecimal price = null;

        if ( entity.getDistancia_metros() != null ) {
            distanciaMetros = entity.getDistancia_metros();
        }
        id = entity.getId();
        title = entity.getTitle();
        description = entity.getDescription();
        startDate = entity.getStartDate();
        endDate = entity.getEndDate();
        enderecoExtenso = entity.getEnderecoExtenso();
        organizador = entity.getOrganizador();
        imageUrl = entity.getImageUrl();
        estadoEvento = entity.getEstadoEvento();
        price = entity.getPrice();

        double lat = entity.getLocalizacao().getY();
        double lng = entity.getLocalizacao().getX();
        Point local = null;

        EventoDTOResponseDistance eventoDTOResponseDistance = new EventoDTOResponseDistance( id, title, description, startDate, endDate, local, enderecoExtenso, organizador, imageUrl, estadoEvento, price, lat, lng, distanciaMetros );

        return eventoDTOResponseDistance;
    }

    @Override
    public void updateEntityFromDto(EventoDTORequest dto, EventoEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.title() != null ) {
            entity.setTitle( dto.title() );
        }
        if ( dto.description() != null ) {
            entity.setDescription( dto.description() );
        }
        if ( dto.startDate() != null ) {
            entity.setStartDate( dto.startDate() );
        }
        if ( dto.endDate() != null ) {
            entity.setEndDate( dto.endDate() );
        }
        if ( dto.enderecoExtenso() != null ) {
            entity.setEnderecoExtenso( dto.enderecoExtenso() );
        }
        if ( dto.organizador() != null ) {
            entity.setOrganizador( dto.organizador() );
        }
        if ( dto.imageUrl() != null ) {
            entity.setImageUrl( dto.imageUrl() );
        }
        if ( dto.price() != null ) {
            entity.setPrice( dto.price() );
        }
        if ( entity.getConfirmacoes() != null ) {
            Set<UserEntity> set = userDTODetailsSetToUserEntitySet( dto.confirmacoes() );
            if ( set != null ) {
                entity.getConfirmacoes().clear();
                entity.getConfirmacoes().addAll( set );
            }
        }
        else {
            Set<UserEntity> set = userDTODetailsSetToUserEntitySet( dto.confirmacoes() );
            if ( set != null ) {
                entity.setConfirmacoes( set );
            }
        }
        if ( dto.estadoEvento() != null ) {
            entity.setEstadoEvento( dto.estadoEvento() );
        }
        if ( entity.getComentarios() != null ) {
            Set<ComentarioEntity> set1 = comentariosDTOSetToComentarioEntitySet( dto.comentarios() );
            if ( set1 != null ) {
                entity.getComentarios().clear();
                entity.getComentarios().addAll( set1 );
            }
        }
        else {
            Set<ComentarioEntity> set1 = comentariosDTOSetToComentarioEntitySet( dto.comentarios() );
            if ( set1 != null ) {
                entity.setComentarios( set1 );
            }
        }
        if ( entity.getCategorias() != null ) {
            Set<CategoriaEntity> set2 = dto.categorias();
            if ( set2 != null ) {
                entity.getCategorias().clear();
                entity.getCategorias().addAll( set2 );
            }
        }
        else {
            Set<CategoriaEntity> set2 = dto.categorias();
            if ( set2 != null ) {
                entity.setCategorias( new LinkedHashSet<CategoriaEntity>( set2 ) );
            }
        }
        if ( entity.getAvaliacoes() != null ) {
            Set<AvaliacaoEntity> set3 = avaliacaoDTOSetToAvaliacaoEntitySet( dto.avaliacoes() );
            if ( set3 != null ) {
                entity.getAvaliacoes().clear();
                entity.getAvaliacoes().addAll( set3 );
            }
        }
        else {
            Set<AvaliacaoEntity> set3 = avaliacaoDTOSetToAvaliacaoEntitySet( dto.avaliacoes() );
            if ( set3 != null ) {
                entity.setAvaliacoes( set3 );
            }
        }
    }

    protected UserEntity userDTODetailsToUserEntity(UserDTODetails userDTODetails) {
        if ( userDTODetails == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setNome( userDTODetails.nome() );
        userEntity.setSobrenome( userDTODetails.sobrenome() );
        userEntity.setSexo( userDTODetails.sexo() );
        userEntity.setEmail( userDTODetails.email() );
        userEntity.setFcmToken( userDTODetails.fcmToken() );
        userEntity.setIdade( userDTODetails.idade() );
        List<EventoEntity> list = userDTODetails.eventos();
        if ( list != null ) {
            userEntity.setEventos( new ArrayList<EventoEntity>( list ) );
        }
        List<EventoEntity> list1 = userDTODetails.historicoEventos();
        if ( list1 != null ) {
            userEntity.setHistoricoEventos( new ArrayList<EventoEntity>( list1 ) );
        }

        return userEntity;
    }

    protected Set<UserEntity> userDTODetailsSetToUserEntitySet(Set<UserDTODetails> set) {
        if ( set == null ) {
            return null;
        }

        Set<UserEntity> set1 = new LinkedHashSet<UserEntity>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( UserDTODetails userDTODetails : set ) {
            set1.add( userDTODetailsToUserEntity( userDTODetails ) );
        }

        return set1;
    }

    protected ComentarioEntity comentariosDTOToComentarioEntity(ComentariosDTO comentariosDTO) {
        if ( comentariosDTO == null ) {
            return null;
        }

        ComentarioEntity comentarioEntity = new ComentarioEntity();

        comentarioEntity.setUser( comentariosDTO.user() );
        comentarioEntity.setTitle( comentariosDTO.title() );
        comentarioEntity.setComentario( comentariosDTO.comentario() );
        comentarioEntity.setEvento( comentariosDTO.evento() );

        return comentarioEntity;
    }

    protected Set<ComentarioEntity> comentariosDTOSetToComentarioEntitySet(Set<ComentariosDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<ComentarioEntity> set1 = new LinkedHashSet<ComentarioEntity>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( ComentariosDTO comentariosDTO : set ) {
            set1.add( comentariosDTOToComentarioEntity( comentariosDTO ) );
        }

        return set1;
    }

    protected AvaliacaoEntity avaliacaoDTOToAvaliacaoEntity(AvaliacaoDTO avaliacaoDTO) {
        if ( avaliacaoDTO == null ) {
            return null;
        }

        AvaliacaoEntity avaliacaoEntity = new AvaliacaoEntity();

        avaliacaoEntity.setCreatedAt( avaliacaoDTO.createdAt() );
        avaliacaoEntity.setId( avaliacaoDTO.id() );
        avaliacaoEntity.setNota( avaliacaoDTO.nota() );
        avaliacaoEntity.setUser( avaliacaoDTO.user() );
        avaliacaoEntity.setEvento( avaliacaoDTO.evento() );

        return avaliacaoEntity;
    }

    protected Set<AvaliacaoEntity> avaliacaoDTOSetToAvaliacaoEntitySet(Set<AvaliacaoDTO> set) {
        if ( set == null ) {
            return null;
        }

        Set<AvaliacaoEntity> set1 = new LinkedHashSet<AvaliacaoEntity>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( AvaliacaoDTO avaliacaoDTO : set ) {
            set1.add( avaliacaoDTOToAvaliacaoEntity( avaliacaoDTO ) );
        }

        return set1;
    }
}
