package com.example.roleslz_backend.Tables.events.services;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOSumary;
import com.example.roleslz_backend.Tables.events.DTO.PaginationWraper;
import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.OrigemParticipacao;
import com.example.roleslz_backend.Tables.events.entity.ParticipacaoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import com.example.roleslz_backend.Tables.users.services.UserUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipacaoService {

    private static final int DIAS_JANELA_CONFIRMACAO = 14;

    private final ParticipacaoRepository participacaoRepository;
    private final EventoRepository eventoRepository;
    private final UserRepository userRepository;
    private final UserUtils userUtils;

    public ParticipacaoService(ParticipacaoRepository participacaoRepository, EventoRepository eventoRepository,
                               UserRepository userRepository, UserUtils userUtils) {
        this.participacaoRepository = participacaoRepository;
        this.eventoRepository = eventoRepository;
        this.userRepository = userRepository;
        this.userUtils = userUtils;
    }

    /**
     * Resposta "sim, eu fui" ao prompt pós-evento. Só é aceita se o usuário tinha demonstrado
     * INTERESSE no evento (ou já tem presença registrada) — nunca como forma de reivindicar
     * um evento qualquer. Presença DECLARADA não é verificada por GPS.
     */
    @Transactional
    public void confirmarPresencaDeclarada(long eventoId) {
        long userId = userUtils.getUserLogado().getId();
        EventoEntity evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EventNotFounded("Evento não encontrado"));

        if (evento.getEstadoEvento() != EstadoEvento.ENCERRADO) {
            throw new IllegalStateException("Só é possível confirmar presença em evento já encerrado.");
        }

        if (participacaoRepository.existsByUserIdAndEventoId(userId, eventoId)) {
            return; // idempotente
        }

        boolean tinhaInteresse = evento.getConfirmacoes() != null
                && evento.getConfirmacoes().stream().anyMatch(u -> u.getId() == userId);
        if (!tinhaInteresse) {
            throw new IllegalStateException("Você não demonstrou interesse neste evento antes dele acontecer.");
        }

        UserEntity user = userRepository.findById(userId).orElseThrow();
        participacaoRepository.save(new ParticipacaoEntity(user, evento, OrigemParticipacao.DECLARADO));
    }

    /** Histórico: eventos em que o usuário logado esteve, do mais recente para o mais antigo. */
    public PaginationWraper getHistorico(int pagina, int itens) {
        Page<EventoEntity> page = participacaoRepository.findEventosParticipadosByUserId(
                userUtils.getUserLogado().getId(), PageRequest.of(pagina, itens));

        return new PaginationWraper(
                page.getContent().stream().map(EventDTOSumary::toSummaryDTO).toList(),
                page.getNumber(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    /** Eventos aguardando a resposta "você foi?" do usuário logado (para o card in-app). */
    public List<EventDTOSumary> getPendentesConfirmacao() {
        LocalDateTime desde = LocalDateTime.now().minusDays(DIAS_JANELA_CONFIRMACAO);
        return eventoRepository.findPendentesConfirmacaoPresenca(userUtils.getUserLogado().getId(), desde)
                .stream()
                .map(EventDTOSumary::toSummaryDTO)
                .toList();
    }
}
