package com.example.roleslz_backend.Utills.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Garante os índices que o Hibernate não cria.
 *
 * <p><b>Por que isso existe.</b> O schema vem de {@code ddl-auto=update}, que cria tabelas
 * e colunas a partir das entidades — mas nenhuma entidade declara @Index, e o Flyway está
 * desligado, então os CREATE INDEX que existem em {@code src/main/resources/db/migration}
 * nunca rodam. Resultado: as consultas espaciais do mapa faziam varredura sequencial.
 *
 * <p>Há ainda um índice que JPA simplesmente não sabe expressar: o GIST da coluna
 * {@code geometry}. A anotação @Index do JPA só gera B-tree, e B-tree é inútil para o
 * operador {@code &&} de bounding box. Esse precisa ser SQL na mão, de qualquer forma.
 *
 * <p>Todos os comandos são {@code IF NOT EXISTS}: rodar de novo não custa nada, o que faz
 * disto algo seguro em todo boot, em banco novo ou já povoado. Falha de um índice é
 * registrada e não impede os outros nem a subida da aplicação — um índice ausente deixa
 * o app lento, não quebrado, e derrubar o processo por isso seria pior.
 *
 * <p>Isto é uma ponte honesta, não o destino final: o certo é o Flyway cuidar do schema.
 * Hoje não dá, porque as migrations existentes são remendos incrementais sobre um schema
 * criado pelo Hibernate (não constroem o banco do zero) e os nomes dos arquivos não seguem
 * a convenção que o Flyway exige.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spotrole.indices.auto-criar", matchIfMissing = true)
public class DatabaseIndexInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseIndexInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Índices por ordem de impacto.
     *
     * <p>Não estão aqui, de propósito:
     * <ul>
     *   <li>{@code tb_presenca_ativa(user_id)} e {@code participacao_evento(user_id)} — a
     *       restrição UNIQUE (user_id, evento_id) dessas tabelas já cria um índice composto,
     *       e {@code user_id} é a primeira coluna dele, então consultas por usuário já estão
     *       atendidas. Criar outro seria índice morto, custando escrita a cada check-in.</li>
     *   <li>{@code users(email)} — a coluna é {@code unique = true}, então o Postgres já
     *       mantém índice. É a consulta mais quente do app (o filtro de segurança resolve o
     *       e-mail do token em toda requisição) e felizmente já estava coberta.</li>
     * </ul>
     */
    private static final List<String> INDICES = List.of(
            // O mais importante: sem GIST, todo bounding box do heatmap e todo ST_DWithin
            // do "eventos próximos" varrem a tabela spot inteira, linha por linha.
            "CREATE INDEX IF NOT EXISTS idx_spot_localizacao ON spot USING GIST (localizacao)",

            // FK não ganha índice automático no Postgres. Estas duas são o caminho quente:
            // o LEFT JOIN do heatmap e a limpeza pós-evento filtram por evento_id.
            "CREATE INDEX IF NOT EXISTS idx_presenca_ativa_evento ON tb_presenca_ativa (evento_id)",
            "CREATE INDEX IF NOT EXISTS idx_participacao_evento ON participacao_evento (evento_id)",

            // Join eventos -> spot do heatmap.
            "CREATE INDEX IF NOT EXISTS idx_eventos_spot ON eventos (spot_id)",

            // Os três schedulers filtram por estado + data. Composto porque é sempre
            // a dupla junta, e a ordem importa: estado primeiro (muito seletivo),
            // data depois (permite varrer uma faixa).
            "CREATE INDEX IF NOT EXISTS idx_eventos_estado_start ON eventos (estado_evento, start_date)",
            "CREATE INDEX IF NOT EXISTS idx_eventos_estado_end ON eventos (estado_evento, end_date)",

            // "Meus rolês" do organizador.
            "CREATE INDEX IF NOT EXISTS idx_eventos_owner ON eventos (user_id)",

            // Seguidores: a PK da tabela de junção começa em follower_id, então "quem eu sigo"
            // já está coberto; "quem me segue" (followed_id) não estava.
            "CREATE INDEX IF NOT EXISTS idx_user_relationships_followed ON user_relationships (followed_id)",

            // Limpeza de tokens mortos reportados pelo FCM (UPDATE ... WHERE fcm_token IN (...)).
            "CREATE INDEX IF NOT EXISTS idx_users_fcm_token ON users (fcm_token)"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void garantirIndices() {
        int criados = 0;
        int falhas = 0;

        for (String ddl : INDICES) {
            try {
                jdbcTemplate.execute(ddl);
                criados++;
            } catch (Exception e) {
                falhas++;
                log.warn("Não foi possível garantir um índice. SQL: {} | Motivo: {}", ddl, e.getMessage());
            }
        }

        if (falhas == 0) {
            log.info("Índices do banco verificados: {} comando(s) aplicado(s) sem erro.", criados);
        } else {
            log.warn("Índices do banco: {} ok, {} com falha. A aplicação segue, mas consultas "
                    + "do mapa podem ficar lentas até o índice existir.", criados, falhas);
        }
    }
}
