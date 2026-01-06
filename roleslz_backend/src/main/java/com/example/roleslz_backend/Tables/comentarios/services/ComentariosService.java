package com.example.roleslz_backend.Tables.comentarios.services;

import com.example.roleslz_backend.Tables.comentarios.DTO.ComentariosDTO;
import com.example.roleslz_backend.Tables.comentarios.entity.ComentarioEntity;
import com.example.roleslz_backend.Tables.comentarios.exceptions.*;
import com.example.roleslz_backend.Tables.comentarios.mappers.ComentariosMapper;
import com.example.roleslz_backend.Tables.comentarios.repository.ComentariosRepository;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ComentariosService {

    private final ComentariosRepository comentariosRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;
    private final ComentariosMapper comentariosMapper;

    public ComentariosService(ComentariosRepository comentariosRepository, EventoRepository eventoRepository, UserRepository userRepository, ComentariosMapper comentariosMapper) {
        this.comentariosRepository = comentariosRepository;
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
        this.comentariosMapper = comentariosMapper;
    }

    public ComentariosDTO addComentario(ComentariosDTO comentariosDTO){

        UserEntity user = userRepository.findById(comentariosDTO.user().getId()).orElseThrow(()-> new UserNotFounded("Usuário não encontrado"));

        EventoEntity evento = eventoRepository.findById(comentariosDTO.evento().getId()).orElseThrow(()-> new EventNotFounded("Evento não encontrado"));

        comentariosRepository.findFirstByUserAndEventoOrderByCreatedAtDesc(user, evento).ifPresent(
                ultimo -> {
                    long segundos = java.time.Duration.between(ultimo.getCreatedAt(), java.time.LocalDateTime.now()).getSeconds();

                    if(segundos < 30){
                        throw new TimeDelayComentario("Aguarde " + (40 - segundos) + " segundos para comentar novamente.");
                    }

                    if (ultimo.getComentario().equalsIgnoreCase(comentariosDTO.comentario())){
                        throw new ComentarioDuplicado("Não comente o mesmo conteúdo, não faça spam.");
                    }
                }
        );

        ComentarioEntity novoComentario = new ComentarioEntity();

        novoComentario.setUser(user);
        novoComentario.setEvento(evento);
        novoComentario.setTitle(comentariosDTO.title());
        novoComentario.setComentario(comentariosDTO.comentario());

        try{
            ComentarioEntity salvo = comentariosRepository.save(novoComentario);
            return comentariosMapper.toDTO(salvo);
        } catch (Exception e) {
            throw new ComentarioNãoAdicionado("Comentário não adicionado");
        }
    }

    public void deleteComentario(long comentarioId){
        ComentarioEntity comentario = comentariosRepository.findById(comentarioId).orElseThrow(()-> new ComentarioNotFounded("Comentário não encontrado"));

        try{
            comentariosRepository.deleteById(comentarioId);
        } catch (Exception e) {
            throw new ComentarioNotDeleted("Comentário não deletado");
        }

    }

    public ComentariosDTO editComentario(long comentarioId, ComentariosDTO comentariosDTO){
        ComentarioEntity comentario = comentariosRepository.findById(comentarioId).orElseThrow(()-> new ComentarioNotFounded("Comentário não encontrado"));

        if (comentario.getComentario().equalsIgnoreCase(comentariosDTO.comentario())){
            throw new ComentarioDuplicado("Comentário duplicado");
        }

        comentario.setComentario(comentariosDTO.comentario());

        try{
            ComentarioEntity retorno = comentariosRepository.save(comentario);
            return comentariosMapper.toDTO(retorno);
        } catch (Exception e) {
            throw new ComentarioNotEdited("Comentário não editado");
        }
    }
}
