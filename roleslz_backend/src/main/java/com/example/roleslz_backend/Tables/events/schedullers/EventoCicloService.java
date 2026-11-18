package com.example.roleslz_backend.Tables.events.schedullers;

import com.example.roleslz_backend.Tables.events.entity.EstadoEvento;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.entity.ParticipacaoEntity;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.repository.ParticipacaoRepository;
import com.example.roleslz_backend.Tables.events.repository.PresencaAtivaRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Utills.notificationsServices.PushJob;
import com.example.roleslz_backend.Utills.notificationsServices.UtillsNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A parte transacional do ciclo de vida dos eventos.
 *
 * <p>Separado de {@link EventoSchedullers} por dois motivos que valem registrar:
 *
 * <p><b>1. A transação não pode esperar a rede.</b> Antes, o push ao Firebase acontecia
 * dentro do @Transactional: a conexão do pool ficava reservada durante a ida e volta
 * até o Google. Com 50 eventos começando num sábado, o scheduler sozinho conseguia
 * ocupar o pool inteiro e fazer requisição de usuário esperar por conexão. Agora cada
 * método abre a transação, faz só trabalho de banco, devolve a conexão — e entrega ao
 * chamador uma lista de {@link PushJob} pronta para ser despachada fora dela.
 *
 * <p><b>2. @Transactional é um proxy.</b> Se estes métodos morassem na mesma classe dos
 * métodos @Scheduled e fossem chamados internamente, o proxy não seria atravessado e a
 * anotação simplesmente não valeria — sem erro, sem aviso, sem transação.
 */
@Service
public class EventoCicloService {

    private static final System.Logger LOGGER = System.getLogger(EventoCicloService.class.getName());

    static final String MSG_30_PRE = "Seu evento irá começar em 30 minutos!";
    static final String MSG_5_PRE = "Seu evento começou!";

    private final EventoRepository eventoRepository;
    private final UtillsNotification utillsNotification;
    private final PresencaAtivaRepository presencaAtivaRepository;
    private final ParticipacaoRepository participacaoRepository;

    public EventoCicloService(EventoRepository eventoRepository, UtillsNotification utillsNotification,
                              PresencaAtivaRepository presencaAtivaRepository,
                              ParticipacaoRepository participacaoRepository) {
        this.eventoRepository = eventoRepository;
        this.utillsNotification = utillsNotification;
        this.presencaAtivaRepository = presencaAtivaRepository;
        this.participacaoRepository = participacaoRepository;
    }

    /**
     * Avisa quem confirmou e promove PRE -> DURANTE.
     *
     * @return os pushes a disparar DEPOIS do commit.
     */
    @Transactional
    public List<PushJob> iniciarEventos() {
        LocalDateTime agora = LocalDateTime.now();
        List<PushJob> pushes = new ArrayList<>();

        // 1. Aviso de 30 minutos (eventos que começam entre 29 e 30 minutos a partir de agora).
        List<EventoEntity> eventos30pos = eventoRepository
                .findByEstadoEventoAndStartDateBetween(EstadoEvento.PRE, agora.plusMinutes(29), agora.plusMinutes(30))
                .orElse(List.of());

        for (EventoEntity evento : eventos30pos) {
            try {
                pushes.add(utillsNotification.montarInicioDeEvento(evento, MSG_30_PRE));
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao montar push (30min) do evento ID: " + evento.getId(), e);
            }
        }

        // 2. Eventos começando agora ou atrasados (viram DURANTE).
        // OBS: 'Before' aqui é intencional — tudo que atrasou e ainda é PRE DEVE virar DURANTE.
        List<EventoEntity> eventos5pos = eventoRepository
                .findByEstadoEventoAndStartDateBefore(EstadoEvento.PRE, agora.plusMinutes(5))
                .orElse(List.of());

        List<EventoEntity> eventosParaSalvar = new ArrayList<>();

        for (EventoEntity evento : eventos5pos) {
            // Notificação é efeito colateral best-effort: a falha dela NÃO pode impedir
            // a transição de estado (senão o evento ficaria preso em PRE para sempre).
            try {
                pushes.add(utillsNotification.montarInicioDeEvento(evento, MSG_5_PRE));
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao montar push (início) do evento ID: " + evento.getId(), e);
            }

            try {
                evento.setEstadoEvento(EstadoEvento.DURANTE);
                eventosParaSalvar.add(evento);
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao iniciar evento ID: " + evento.getId(), e);
            }
        }

        if (!eventosParaSalvar.isEmpty()) {
            eventoRepository.saveAll(eventosParaSalvar);
        }

        return pushes;
    }

    /**
     * Promove DURANTE -> ENCERRADO e monta os pushes pós-rolê.
     *
     * @return os pushes a disparar DEPOIS do commit.
     */
    @Transactional
    public List<PushJob> encerrarEventos() {
        LocalDateTime agora = LocalDateTime.now();

        List<EventoEntity> eventosParaEncerrar = eventoRepository
                .findByEstadoEventoAndEndDateBeforeAndPresencaLimpaFalse(EstadoEvento.DURANTE, agora)
                .orElse(List.of());

        List<EventoEntity> eventosParaSalvar = new ArrayList<>();
        List<PushJob> pushes = new ArrayList<>();

        for (EventoEntity evento : eventosParaEncerrar) {
            try {
                evento.setEstadoEvento(EstadoEvento.ENCERRADO);
                eventosParaSalvar.add(evento);
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao encerrar evento ID: " + evento.getId(), e);
            }

            // Best-effort: nunca pode impedir a transição DURANTE -> ENCERRADO.
            try {
                pushes.addAll(montarPushesPosEvento(evento));
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "Erro ao montar pushes pós-evento do evento ID: " + evento.getId(), e);
            }
        }

        if (!eventosParaSalvar.isEmpty()) {
            eventoRepository.saveAll(eventosParaSalvar);
        }

        return pushes;
    }

    /**
     * Quando o evento encerra:
     *  - quem ESTEVE lá (participacao_evento) recebe o convite para avaliar;
     *  - quem demonstrou INTERESSE mas não teve presença detectada recebe o prompt
     *    "você foi?" — fallback para o geofence que não disparou.
     */
    private List<PushJob> montarPushesPosEvento(EventoEntity evento) {
        List<PushJob> pushes = new ArrayList<>();

        List<ParticipacaoEntity> participacoes = participacaoRepository.findByEventoId(evento.getId());
        Set<Long> jaParticiparam = participacoes.stream()
                .map(p -> p.getUser().getId())
                .collect(Collectors.toSet());

        List<UserEntity> participantes = participacoes.stream().map(ParticipacaoEntity::getUser).toList();
        if (!participantes.isEmpty()) {
            pushes.add(utillsNotification.montarAvaliacaoPosRole(evento, participantes));
        }

        Set<UserEntity> interessados = evento.getConfirmacoes();
        if (interessados != null) {
            List<UserEntity> perguntar = interessados.stream()
                    .filter(u -> !jaParticiparam.contains(u.getId()))
                    .toList();
            if (!perguntar.isEmpty()) {
                pushes.add(utillsNotification.montarPerguntaDePresenca(evento, perguntar));
            }
        }

        return pushes;
    }

    /**
     * Apaga o cache de presenças de eventos já encerrados (o que alimenta o heatmap).
     * Não gera push nenhum.
     *
     * @return quantos eventos foram limpos.
     */
    @Transactional
    public int limparHeatmap() {
        LocalDateTime agora = LocalDateTime.now();

        // Só os que ainda não foram limpos — sem isso a varredura cresceria para sempre.
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

        return eventosSalvar.size();
    }
}
