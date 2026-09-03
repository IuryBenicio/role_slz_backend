-- ============================================================================
--  SEED DE DADOS FAKE — eventos, spots, check-ins (heatmap) e avaliações
--  Uso:
--    docker compose up -d db
--    psql "postgresql://iurybenicio:SUA_SENHA@localhost:5432/spotrole" -f scripts/seed_fake_events.sql
--  (ou:  docker compose exec -T db psql -U iurybenicio -d spotrole < scripts/seed_fake_events.sql)
--
--  Rode DEPOIS que a API tiver subido pelo menos uma vez (Hibernate cria as tabelas).
--  É idempotente: apaga tudo que tem prefixo [FAKE] antes de inserir de novo.
-- ============================================================================

BEGIN;

-- ---------------------------------------------------------------------------
-- 1. Limpa dados [FAKE] de execuções anteriores
-- ---------------------------------------------------------------------------
DELETE FROM tb_presenca_ativa   WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM participacao_evento WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM eventos_confirmacoes WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM avaliacao           WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM evento_galery       WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM eventos             WHERE title LIKE '[FAKE]%';
DELETE FROM spot                WHERE nome LIKE '[FAKE]%';
DELETE FROM users               WHERE email LIKE 'fake+%@spotrole.dev';

-- ---------------------------------------------------------------------------
-- 2. Usuários fake: 2 organizadores + 12 "presentes" (pra alimentar o heatmap)
-- ---------------------------------------------------------------------------
-- Obs.: coluna "role" é mapeada como ORDINAL no backend -> REGULAR = 1.
INSERT INTO users (nome, sobrenome, email, password, sexo, idade, is_enable, role, created_at)
SELECT
    'Fake', 'User' || g,
    'fake+' || g || '@spotrole.dev',
    '$2a$10$abcdefghijklmnopqrstuv',           -- hash dummy (esses users nunca logam)
    (ARRAY['MASCULINO','FEMININO','NAO_ESPECIFICADO'])[1 + (g % 3)],
    18 + (g % 25),
    true, 1, now()
FROM generate_series(1, 14) AS g;

-- ---------------------------------------------------------------------------
-- 3. Spots espalhados por São Luís
-- ---------------------------------------------------------------------------
INSERT INTO spot (nome, localizacao) VALUES
    ('[FAKE] Litorânea',        ST_SetSRID(ST_MakePoint(-44.3025, -2.4930), 4326)),
    ('[FAKE] Ponta d''Areia',   ST_SetSRID(ST_MakePoint(-44.3110, -2.4880), 4326)),
    ('[FAKE] Renascença',       ST_SetSRID(ST_MakePoint(-44.2885, -2.4995), 4326)),
    ('[FAKE] Centro Histórico', ST_SetSRID(ST_MakePoint(-44.3060, -2.5290), 4326)),
    ('[FAKE] Calhau',           ST_SetSRID(ST_MakePoint(-44.2530, -2.4780), 4326)),
    ('[FAKE] Cohama',           ST_SetSRID(ST_MakePoint(-44.2610, -2.5205), 4326)),
    ('[FAKE] São Francisco',    ST_SetSRID(ST_MakePoint(-44.2835, -2.5045), 4326)),
    ('[FAKE] Olho d''Água',     ST_SetSRID(ST_MakePoint(-44.2360, -2.4990), 4326));

-- ---------------------------------------------------------------------------
-- 4. Eventos — mistura de estados. Datas relativas a now() (sempre "fresco").
--    org = 1º organizador fake.  spot = por nome.
-- ---------------------------------------------------------------------------
WITH org AS (SELECT id FROM users WHERE email = 'fake+1@spotrole.dev'),
     s AS (SELECT nome, id FROM spot WHERE nome LIKE '[FAKE]%')
INSERT INTO eventos
    (title, description, start_date, end_date, endereco_ext, event_type, event_privacity,
     user_id, price, ticket_link, spot_id, presenca_limpa, estado_evento, exibir_confirmacoes_publicamente, created_at)
SELECT v.title, v.description, v.start_date, v.end_date, v.endereco, 'PERSONAL', 'PUBLICO',
       (SELECT id FROM org), v.price, v.ticket_link,
       (SELECT id FROM s WHERE s.nome = v.spot_nome),
       false, v.estado, true, now()
FROM (VALUES
    -- ACONTECENDO AGORA (entram no heatmap e nos pinos do mapa)
    ('[FAKE] Luau na Litorânea',   'Som, fogueira e pôr do sol.',        now() - interval '1 hour',  now() + interval '4 hours', 'Av. Litorânea, Calhau',        0.00,   NULL,                              '[FAKE] Litorânea',        'DURANTE'),
    ('[FAKE] Feira Gastronômica',  'Food trucks e música ao vivo.',      now() - interval '30 min',  now() + interval '5 hours', 'Praça Maria Aragão, Centro',    0.00,   NULL,                              '[FAKE] Centro Histórico', 'DURANTE'),
    ('[FAKE] Baile Neon',          'A maior festa neon da ilha.',        now() - interval '20 min',  now() + interval '6 hours', 'Renascença II',                 40.00,  'https://www.sympla.com.br/fake',  '[FAKE] Renascença',       'DURANTE'),
    -- COMEÇANDO EM BREVE (dentro da janela de 90 min do heatmap)
    ('[FAKE] Sunset Ponta d''Areia','DJ set no fim de tarde.',           now() + interval '40 min',  now() + interval '5 hours', 'Ponta d''Areia',                25.00,  'https://www.sympla.com.br/fake',  '[FAKE] Ponta d''Areia',   'PRE'),
    ('[FAKE] Roda de Samba',       'Samba de raiz no quintal.',          now() + interval '75 min',  now() + interval '4 hours', 'Rua da Alegria, São Francisco', 0.00,   NULL,                              '[FAKE] São Francisco',    'PRE'),
    -- FUTUROS (entram no Feed: HOJE / SEMANA / MÊS)
    ('[FAKE] Festa Junina do Calhau','Quadrilha, forró e comida típica.', now() + interval '6 hours', now() + interval '12 hours','Praia do Calhau',              15.00,  NULL,                              '[FAKE] Calhau',           'PRE'),
    ('[FAKE] Show de Reggae',      'Clássicos do reggae maranhense.',    now() + interval '3 days',   now() + interval '3 days' + interval '5 hours','Cohama',        30.00,  'https://www.sympla.com.br/fake',  '[FAKE] Cohama',           'PRE'),
    ('[FAKE] Praia Party',         'Open bar até o sol raiar.',          now() + interval '9 days',   now() + interval '9 days' + interval '8 hours','Olho d''Água',  80.00,  'https://www.ingresse.com/fake',   '[FAKE] Olho d''Água',     'PRE'),
    -- JÁ ROLARAM (pra testar histórico / avaliação)
    ('[FAKE] Aniversário do Bar X','Rolê que já aconteceu.',             now() - interval '3 days',   now() - interval '3 days' + interval '4 hours','Renascença',    0.00,   NULL,                              '[FAKE] Renascença',       'ENCERRADO'),
    ('[FAKE] Esquenta de Carnaval','Marchinhas e blocos.',               now() - interval '8 days',   now() - interval '8 days' + interval '6 hours','Centro',        0.00,   NULL,                              '[FAKE] Centro Histórico', 'ENCERRADO')
) AS v(title, description, start_date, end_date, endereco, price, ticket_link, spot_nome, estado);

-- 1 foto por evento
INSERT INTO evento_galery (link, evento_id)
SELECT 'https://picsum.photos/seed/' || e.id || '/800/500', e.id
FROM eventos e WHERE e.title LIKE '[FAKE]%';

-- ---------------------------------------------------------------------------
-- 5. Check-ins ativos (tb_presenca_ativa) -> peso do heatmap.
--    Distribui os 14 users fake nos eventos DURANTE / PRE-em-breve, em quantidades
--    diferentes, pra o mapa de calor ter manchas de intensidades variadas.
-- ---------------------------------------------------------------------------
INSERT INTO tb_presenca_ativa (user_id, evento_id, data_entrada)
SELECT u.id, x.evento_id, now() - (random() * interval '90 minutes')
FROM (
    SELECT e.id AS evento_id,
           CASE e.title
               WHEN '[FAKE] Baile Neon'            THEN 12
               WHEN '[FAKE] Luau na Litorânea'     THEN 8
               WHEN '[FAKE] Feira Gastronômica'    THEN 5
               WHEN '[FAKE] Sunset Ponta d''Areia' THEN 3
               WHEN '[FAKE] Roda de Samba'         THEN 2
               ELSE 0
           END AS qtd
    FROM eventos e WHERE e.title LIKE '[FAKE]%'
) x
JOIN LATERAL (
    SELECT id FROM users WHERE email LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT x.qtd
) u ON true;

-- ---------------------------------------------------------------------------
-- 6. Participação nos eventos ENCERRADOS (histórico) + algumas avaliações
--    Usa users fake — troque pelo bloco do fim se quiser testar com a SUA conta.
-- ---------------------------------------------------------------------------
INSERT INTO participacao_evento (user_id, evento_id, check_in_at, origem)
SELECT u.id, e.id, e.start_date + interval '30 minutes', 'GEOFENCE'
FROM eventos e
JOIN LATERAL (SELECT id FROM users WHERE email LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT 6) u ON true
WHERE e.title IN ('[FAKE] Aniversário do Bar X', '[FAKE] Esquenta de Carnaval');

INSERT INTO avaliacao (nota, comentario, user_id, evento_id, created_at)
SELECT (3 + (u.rn % 3)), (ARRAY['Foi ótimo!','Curti bastante.','Podia ter mais espaço.'])[1 + (u.rn % 3)],
       u.id, e.id, e.end_date + interval '1 day'
FROM eventos e
JOIN LATERAL (
    SELECT id, row_number() OVER () AS rn
    FROM users WHERE email LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT 4
) u ON true
WHERE e.title = '[FAKE] Aniversário do Bar X';

COMMIT;

-- ============================================================================
--  OPCIONAL: vincular a SUA conta (a que você registrou no app) a alguns
--  eventos, pra testar Agenda / "você foi?" / avaliar.
--  Descomente e troque o e-mail:
-- ============================================================================
-- BEGIN;
-- WITH me AS (SELECT id FROM users WHERE email = 'SEU_EMAIL_AQUI')
-- INSERT INTO eventos_confirmacoes (evento_id, user_id)
-- SELECT e.id, (SELECT id FROM me) FROM eventos e
-- WHERE e.title IN ('[FAKE] Show de Reggae', '[FAKE] Praia Party', '[FAKE] Esquenta de Carnaval')
-- ON CONFLICT DO NOTHING;
--
-- WITH me AS (SELECT id FROM users WHERE email = 'SEU_EMAIL_AQUI')
-- INSERT INTO participacao_evento (user_id, evento_id, check_in_at, origem)
-- SELECT (SELECT id FROM me), e.id, e.start_date + interval '20 minutes', 'GEOFENCE'
-- FROM eventos e WHERE e.title = '[FAKE] Aniversário do Bar X'
-- ON CONFLICT DO NOTHING;
-- COMMIT;

SELECT estado_evento, count(*) FROM eventos WHERE title LIKE '[FAKE]%' GROUP BY estado_evento;
