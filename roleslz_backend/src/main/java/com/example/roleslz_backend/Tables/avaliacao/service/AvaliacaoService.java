package com.example.roleslz_backend.Tables.avaliacao.service;

import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoRequestDTO;
import com.example.roleslz_backend.Tables.avaliacao.DTO.AvaliacaoResponseDTO;
import com.example.roleslz_backend.Tables.avaliacao.entity.AvaliacaoEntity;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoAlreadyExists;
import com.example.roleslz_backend.Tables.avaliacao.exceptions.AvaliacaoDoesntExists;
import com.example.roleslz_backend.Tables.avaliacao.repository.AvaliacaoRepository;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;
    private final ParticipacaoRepository participacaoRepository;
    private final UserUtils userUtils;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, EventoRepository eventoRepository,
                            UserRepository userRepository, ParticipacaoRepository participacaoRepository, UserUtils userUtils) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
        this.participacaoRepository = participacaoRepository;
        this.userUtils = userUtils;
    }

    /**
     * Cria a avaliação do usuário logado para um evento. Só é permitida se:
     *  - o evento já encerrou;
     *  - o usuário tem presença confirmada nele (participacao_evento);
     *  - ele ainda não avaliou este evento.
     */
    @Transactional
    public AvaliacaoResponseDTO criarAvaliacao(long eventoId, AvaliacaoRequestDTO dto) {
        validarNota(dto.nota());
        long userId = userUtils.getUserLogado().getId();
        EventoEntity evento = buscarEventoAvaliavel(eventoId, userId);

        if (avaliacaoRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            throw new AvaliacaoAlreadyExists("Você já avaliou este evento. Edite a avaliação existente.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow();

        AvaliacaoEntity avaliacao = new AvaliacaoEntity();
        avaliacao.setUser(user);
        avaliacao.setEvento(evento);
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(normalizar(dto.comentario()));

        return AvaliacaoResponseDTO.fromEntity(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public AvaliacaoResponseDTO editarAvaliacao(long eventoId, AvaliacaoRequestDTO dto) {
        validarNota(dto.nota());
        long userId = userUtils.getUserLogado().getId();
        AvaliacaoEntity avaliacao = avaliacaoRepository.findByUserIdAndEventoId(userId, eventoId)
                .orElseThrow(() -> new AvaliacaoDoesntExists("Você ainda não avaliou este evento."));

        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(normalizar(dto.comentario()));

        return AvaliacaoResponseDTO.fromEntity(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public void removerAvaliacao(long eventoId) {
        long userId = userUtils.getUserLogado().getId();
        AvaliacaoEntity avaliacao = avaliacaoRepository.findByUserIdAndEventoId(userId, eventoId)
                .orElseThrow(() -> new AvaliacaoDoesntExists("Você ainda não avaliou este evento."));
        avaliacaoRepository.delete(avaliacao);
    }

    private EventoEntity buscarEventoAvaliavel(long eventoId, long userId) {
        EventoEntity evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventNotFounded("Evento não encontrado"));

        if (evento.getEstadoEvento() != EstadoEvento.ENCERRADO) {
            throw new IllegalStateException("Só é possível avaliar um evento depois que ele acontece.");
        }
        if (!participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            throw new IllegalStateException("Só quem esteve no evento pode avaliá-lo.");
        }
        return evento;
    }

    private void validarNota(float nota) {
        if (nota < 1f || nota > 5f) {
            throw new IllegalArgumentException("A nota precisa estar entre 1 e 5.");
        }
    }

    private String normalizar(String comentario) {
        if (comentario == null) return null;
        String limpo = comentario.trim();
        return limpo.isEmpty() ? null : limpo;
    }
}
