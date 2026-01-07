package com.example.roleslz_backend.Tables.events.mapper;

import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.events.DTO.EventoDTO;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
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
    date = "2026-01-06T20:13:06-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class EventoMapperImpl implements EventoMapper {

    @Override
    public EventoEntity toEntity(EventoDTO dto) {
        if ( dto == null ) {
            return null;
        }

        EventoEntity eventoEntity = new EventoEntity();

        eventoEntity.setTitle( dto.title() );
        eventoEntity.setDescription( dto.description() );
        eventoEntity.setStartDate( dto.startDate() );
        eventoEntity.setEndDate( dto.endDate() );
        eventoEntity.setLocal( dto.local() );
        eventoEntity.setEnderecoExtenso( dto.enderecoExtenso() );
        eventoEntity.setOrganizador( dto.organizador() );
        eventoEntity.setImageUrl( dto.imageUrl() );
        eventoEntity.setConfirmacoes( userDTODetailsSetToUserEntitySet( dto.confirmacoes() ) );
        eventoEntity.setEstadoEvento( dto.estadoEvento() );
        Set<ComentarioEntity> set1 = dto.comentarios();
        if ( set1 != null ) {
            eventoEntity.setComentarios( new LinkedHashSet<ComentarioEntity>( set1 ) );
        }
        Set<AvaliacaoEntity> set2 = dto.avaliacoes();
        if ( set2 != null ) {
            eventoEntity.setAvaliacoes( new LinkedHashSet<AvaliacaoEntity>( set2 ) );
        }

        return eventoEntity;
    }

    @Override
    public EventoDTO toDTO(EventoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String title = null;
        String description = null;
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        Point local = null;
        String enderecoExtenso = null;
        UserEntity organizador = null;
        Set<UserDTODetails> confirmacoes = null;
        String imageUrl = null;
        EstadoEvento estadoEvento = null;
        Set<ComentarioEntity> comentarios = null;
        Set<AvaliacaoEntity> avaliacoes = null;

        title = entity.getTitle();
        description = entity.getDescription();
        startDate = entity.getStartDate();
        endDate = entity.getEndDate();
        local = entity.getLocal();
        enderecoExtenso = entity.getEnderecoExtenso();
        organizador = entity.getOrganizador();
        confirmacoes = userEntitySetToUserDTODetailsSet( entity.getConfirmacoes() );
        imageUrl = entity.getImageUrl();
        estadoEvento = entity.getEstadoEvento();
        Set<ComentarioEntity> set1 = entity.getComentarios();
        if ( set1 != null ) {
            comentarios = new LinkedHashSet<ComentarioEntity>( set1 );
        }
        Set<AvaliacaoEntity> set2 = entity.getAvaliacoes();
        if ( set2 != null ) {
            avaliacoes = new LinkedHashSet<AvaliacaoEntity>( set2 );
        }

        EventoDTO eventoDTO = new EventoDTO( title, description, startDate, endDate, local, enderecoExtenso, organizador, confirmacoes, imageUrl, estadoEvento, comentarios, avaliacoes );

        return eventoDTO;
    }

    @Override
    public void updateEntityFromDto(EventoDTO dto, EventoEntity entity) {
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
        if ( dto.local() != null ) {
            entity.setLocal( dto.local() );
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
            Set<ComentarioEntity> set1 = dto.comentarios();
            if ( set1 != null ) {
                entity.getComentarios().clear();
                entity.getComentarios().addAll( set1 );
            }
        }
        else {
            Set<ComentarioEntity> set1 = dto.comentarios();
            if ( set1 != null ) {
                entity.setComentarios( new LinkedHashSet<ComentarioEntity>( set1 ) );
            }
        }
        if ( entity.getAvaliacoes() != null ) {
            Set<AvaliacaoEntity> set2 = dto.avaliacoes();
            if ( set2 != null ) {
                entity.getAvaliacoes().clear();
                entity.getAvaliacoes().addAll( set2 );
            }
        }
        else {
            Set<AvaliacaoEntity> set2 = dto.avaliacoes();
            if ( set2 != null ) {
                entity.setAvaliacoes( new LinkedHashSet<AvaliacaoEntity>( set2 ) );
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

    protected UserDTODetails userEntityToUserDTODetails(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        String nome = null;
        String sobrenome = null;
        Sexo sexo = null;
        String email = null;
        Integer idade = null;
        List<EventoEntity> eventos = null;
        List<EventoEntity> historicoEventos = null;

        nome = userEntity.getNome();
        sobrenome = userEntity.getSobrenome();
        sexo = userEntity.getSexo();
        email = userEntity.getEmail();
        idade = userEntity.getIdade();
        List<EventoEntity> list = userEntity.getEventos();
        if ( list != null ) {
            eventos = new ArrayList<EventoEntity>( list );
        }
        List<EventoEntity> list1 = userEntity.getHistoricoEventos();
        if ( list1 != null ) {
            historicoEventos = new ArrayList<EventoEntity>( list1 );
        }

        UserDTODetails userDTODetails = new UserDTODetails( nome, sobrenome, sexo, email, idade, eventos, historicoEventos );

        return userDTODetails;
    }

    protected Set<UserDTODetails> userEntitySetToUserDTODetailsSet(Set<UserEntity> set) {
        if ( set == null ) {
            return null;
        }

        Set<UserDTODetails> set1 = new LinkedHashSet<UserDTODetails>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( UserEntity userEntity : set ) {
            set1.add( userEntityToUserDTODetails( userEntity ) );
        }

        return set1;
    }
}
