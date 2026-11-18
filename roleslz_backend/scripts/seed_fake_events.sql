-- ============================================================================
--  SEED DE DADOS FAKE — usuários, spots, eventos, heatmap, agenda e avaliações
--
--  Uso:
--    docker compose up -d db
--    docker compose exec -T db psql -U iurybenicio -d spotrole < scripts/seed_fake_events.sql
--
--  Rode DEPOIS que a API tiver subido ao menos uma vez (o Hibernate cria as tabelas).
--  É idempotente: apaga tudo com prefixo [FAKE] antes de inserir de novo.
--
--  As datas são relativas a now(), então o seed nunca "envelhece": rodou, está fresco.
--  A cobertura foi montada em cima das janelas reais do EventoService:
--     AGORA  = start_date entre now()-3h e now()+6h
--     HOJE   = start_date entre now() e hoje 23:59:59
--     SEMANA = start_date entre now() e now()+7 dias   (inclui os de HOJE)
--     MES    = start_date entre now() e now()+1 mês     (inclui os de SEMANA)
--  Por isso cada aba tem eventos SÓ dela, além dos que herda da anterior.
-- ============================================================================

BEGIN;

-- ---------------------------------------------------------------------------
-- 1. Limpa dados [FAKE] de execuções anteriores
-- ---------------------------------------------------------------------------
DELETE FROM tb_presenca_ativa    WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM participacao_evento  WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM eventos_confirmacoes WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM avaliacao            WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM evento_galery        WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%');
DELETE FROM eventos              WHERE title LIKE '[FAKE]%';
DELETE FROM spot                 WHERE nome LIKE '[FAKE]%';
DELETE FROM users                WHERE email LIKE 'fake+%@spotrole.dev';

-- ---------------------------------------------------------------------------
-- 2. Usuários fake — nomes de gente de verdade, porque eles aparecem como
--    autores de avaliação e na lista de confirmados. "Fake User 7" atrapalha
--    na hora de julgar se a tela está boa.
--    Obs.: "role" não tem @Enumerated no UserEntity, então é ORDINAL -> REGULAR = 1.
--          "sexo" é @Enumerated(STRING).
-- ---------------------------------------------------------------------------
INSERT INTO users (nome, sobrenome, email, password, sexo, idade, is_enable, role, created_at)
SELECT v.nome, v.sobrenome,
       'fake+' || v.slug || '@spotrole.dev',
       '$2a$10$abcdefghijklmnopqrstuv',        -- hash dummy: esses usuários nunca logam
       v.sexo, v.idade, true, 1, now()
FROM (VALUES
    ('Ana',      'Ribeiro',   'ana',      'FEMININO',          24),
    ('Bruno',    'Castro',    'bruno',    'MASCULINO',         31),
    ('Carla',    'Mendes',    'carla',    'FEMININO',          27),
    ('Diego',    'Almeida',   'diego',    'MASCULINO',         22),
    ('Elisa',    'Ferreira',  'elisa',    'FEMININO',          29),
    ('Felipe',   'Nunes',     'felipe',   'MASCULINO',         35),
    ('Gabriela', 'Sousa',     'gabriela', 'FEMININO',          20),
    ('Heitor',   'Lima',      'heitor',   'MASCULINO',         28),
    ('Isadora',  'Cunha',     'isadora',  'FEMININO',          33),
    ('João',     'Barros',    'joao',     'MASCULINO',         26),
    ('Kelly',    'Moraes',    'kelly',    'FEMININO',          23),
    ('Lucas',    'Teixeira',  'lucas',    'NAO_ESPECIFICADO',  30)
) AS v(nome, sobrenome, slug, sexo, idade);

-- ---------------------------------------------------------------------------
-- 3. Spots espalhados por São Luís (o heatmap agrupa por spot)
-- ---------------------------------------------------------------------------
INSERT INTO spot (nome, localizacao) VALUES
    ('[FAKE] Litorânea',        ST_SetSRID(ST_MakePoint(-44.3025, -2.4930), 4326)),
    ('[FAKE] Ponta d''Areia',   ST_SetSRID(ST_MakePoint(-44.3110, -2.4880), 4326)),
    ('[FAKE] Renascença',       ST_SetSRID(ST_MakePoint(-44.2885, -2.4995), 4326)),
    ('[FAKE] Centro Histórico', ST_SetSRID(ST_MakePoint(-44.3060, -2.5290), 4326)),
    ('[FAKE] Calhau',           ST_SetSRID(ST_MakePoint(-44.2530, -2.4780), 4326)),
    ('[FAKE] Cohama',           ST_SetSRID(ST_MakePoint(-44.2610, -2.5205), 4326)),
    ('[FAKE] São Francisco',    ST_SetSRID(ST_MakePoint(-44.2835, -2.5045), 4326)),
    ('[FAKE] Olho d''Água',     ST_SetSRID(ST_MakePoint(-44.2360, -2.4990), 4326)),
    ('[FAKE] Praia Grande',     ST_SetSRID(ST_MakePoint(-44.3095, -2.5265), 4326)),
    ('[FAKE] Vinhais',          ST_SetSRID(ST_MakePoint(-44.2455, -2.5120), 4326));

-- ---------------------------------------------------------------------------
-- 4. Eventos, agrupados pela aba do feed em que devem aparecer
-- ---------------------------------------------------------------------------
WITH org AS (SELECT id FROM users WHERE email = 'fake+ana@spotrole.dev'),
     s   AS (SELECT nome, id FROM spot WHERE nome LIKE '[FAKE]%')
INSERT INTO eventos
    (title, description, start_date, end_date, endereco_ext, event_type, event_privacity,
     user_id, price, ticket_link, spot_id, presenca_limpa, estado_evento,
     exibir_confirmacoes_publicamente, created_at)
SELECT v.title, v.description, v.inicio, v.fim, v.endereco, 'PERSONAL', 'PUBLICO',
       (SELECT id FROM org), v.price, v.ticket_link,
       (SELECT id FROM s WHERE s.nome = v.spot_nome),
       false, v.estado, true, now()
FROM (VALUES
    -- === ACONTECENDO AGORA (aba AGORA + pinos e calor no mapa) =============
    ('[FAKE] Luau na Litorânea',      'Som, fogueira e pôr do sol na areia.',    now() - interval '1 hour',   now() + interval '4 hours',  'Av. Litorânea, Calhau',          0.00,  NULL,                             '[FAKE] Litorânea',        'DURANTE', 'AGORA'),
    ('[FAKE] Feira Gastronômica',     'Food trucks e música ao vivo.',           now() - interval '30 minutes', now() + interval '5 hours','Praça Maria Aragão, Centro',     0.00,  NULL,                             '[FAKE] Centro Histórico', 'DURANTE', 'AGORA'),
    ('[FAKE] Baile Neon',             'A maior festa neon da ilha.',             now() - interval '20 minutes', now() + interval '6 hours','Renascença II',                 40.00,  'https://www.sympla.com.br/fake', '[FAKE] Renascença',       'DURANTE', 'AGORA'),
    ('[FAKE] Sunset Ponta d''Areia',  'DJ set no fim de tarde.',                 now() + interval '40 minutes', now() + interval '5 hours','Ponta d''Areia',                25.00,  'https://www.sympla.com.br/fake', '[FAKE] Ponta d''Areia',   'PRE',     'AGORA'),

    -- === HOJE (start_date ainda hoje; os que passarem da meia-noite são descartados)
    ('[FAKE] Roda de Samba',          'Samba de raiz no quintal.',               now() + interval '75 minutes', now() + interval '5 hours','Rua da Alegria, São Francisco',  0.00,  NULL,                             '[FAKE] São Francisco',    'PRE',     'HOJE'),
    ('[FAKE] Karaokê do Vinhais',     'Solta a voz, a bebida é por conta.',      now() + interval '3 hours',    now() + interval '8 hours','Vinhais Velho',                  0.00,  NULL,                             '[FAKE] Vinhais',          'PRE',     'HOJE'),
    ('[FAKE] Sarau na Praia Grande',  'Poesia, MPB e cerveja gelada.',           now() + interval '5 hours',    now() + interval '9 hours','Praia Grande, Centro',          10.00,  NULL,                             '[FAKE] Praia Grande',     'PRE',     'HOJE'),

    -- === ESTA SEMANA (2 a 7 dias) =========================================
    ('[FAKE] Show de Reggae',         'Clássicos do reggae maranhense.',         now() + interval '2 days',  now() + interval '2 days'  + interval '5 hours', 'Cohama',              30.00,  'https://www.sympla.com.br/fake', '[FAKE] Cohama',           'PRE', 'SEMANA'),
    ('[FAKE] Forró do Calhau',        'Trio pé de serra até de madrugada.',      now() + interval '3 days',  now() + interval '3 days'  + interval '6 hours', 'Praia do Calhau',     15.00,  NULL,                             '[FAKE] Calhau',           'PRE', 'SEMANA'),
    ('[FAKE] Cine ao Ar Livre',       'Sessão dupla sob as estrelas.',           now() + interval '5 days',  now() + interval '5 days'  + interval '4 hours', 'Litorânea',            0.00,  NULL,                             '[FAKE] Litorânea',        'PRE', 'SEMANA'),
    ('[FAKE] Feira de Vinil',         'Trocas, discotecagem e cerveja artesanal.', now() + interval '6 days', now() + interval '6 days' + interval '7 hours', 'Renascença',           5.00,  NULL,                             '[FAKE] Renascença',       'PRE', 'SEMANA'),

    -- === ESTE MÊS (8 a 30 dias) ===========================================
    ('[FAKE] Praia Party',            'Open bar até o sol raiar.',               now() + interval '10 days', now() + interval '10 days' + interval '8 hours', 'Olho d''Água',        80.00,  'https://www.ingresse.com/fake',  '[FAKE] Olho d''Água',     'PRE', 'MES'),
    ('[FAKE] Festival de Tambor',     'Tambor de crioula a noite inteira.',      now() + interval '14 days', now() + interval '14 days' + interval '6 hours', 'Praia Grande',         0.00,  NULL,                             '[FAKE] Praia Grande',     'PRE', 'MES'),
    ('[FAKE] Arraial Fora de Época',  'Quadrilha, quentão e correio elegante.',  now() + interval '20 days', now() + interval '20 days' + interval '7 hours', 'Ponta d''Areia',      20.00,  'https://www.sympla.com.br/fake', '[FAKE] Ponta d''Areia',   'PRE', 'MES'),
    ('[FAKE] Virada Eletrônica',      '12 horas de line-up ininterrupto.',       now() + interval '27 days', now() + interval '27 days' + interval '12 hours','Cohama',            120.00,  'https://www.ingresse.com/fake',  '[FAKE] Cohama',           'PRE', 'MES'),

    -- === JÁ ROLARAM (histórico, prompt "você foi?" e avaliação) ============
    ('[FAKE] Aniversário do Bar X',   'Rolê que já aconteceu.',                  now() - interval '3 days',  now() - interval '3 days' + interval '4 hours',  'Renascença',           0.00,  NULL,                             '[FAKE] Renascença',       'ENCERRADO', 'PASSADO'),
    ('[FAKE] Esquenta de Carnaval',   'Marchinhas e blocos.',                    now() - interval '8 days',  now() - interval '8 days' + interval '6 hours',  'Centro',               0.00,  NULL,                             '[FAKE] Centro Histórico', 'ENCERRADO', 'PASSADO'),
    ('[FAKE] Baile Charme',           'Charme antigo no Centro Histórico.',      now() - interval '12 days', now() - interval '12 days' + interval '5 hours', 'Centro Histórico',    10.00,  NULL,                             '[FAKE] Centro Histórico', 'ENCERRADO', 'PASSADO')
) AS v(title, description, inicio, fim, endereco, price, ticket_link, spot_nome, estado, aba)
-- Um evento de "hoje" que caia depois da meia-noite não é de hoje: o feed não o
-- mostraria na aba HOJE e o card ficaria com data de amanhã. Melhor não inserir.
WHERE v.aba <> 'HOJE' OR v.inicio::date = current_date;

-- 1 foto por evento (picsum é estável por seed, então o card não pisca a cada reload)
INSERT INTO evento_galery (link, evento_id)
SELECT 'https://picsum.photos/seed/' || e.id || '/800/500', e.id
FROM eventos e WHERE e.title LIKE '[FAKE]%';

-- ---------------------------------------------------------------------------
-- 5. Check-ins ativos -> peso do heatmap. Quantidades diferentes de propósito,
--    para o mapa ter manchas de intensidades distintas e dar pra comparar.
-- ---------------------------------------------------------------------------
INSERT INTO tb_presenca_ativa (user_id, evento_id, data_entrada)
SELECT u.id, x.evento_id, now() - (random() * interval '90 minutes')
FROM (
    SELECT e.id AS evento_id,
           CASE e.title
               WHEN '[FAKE] Baile Neon'           THEN 12
               WHEN '[FAKE] Luau na Litorânea'    THEN 8
               WHEN '[FAKE] Feira Gastronômica'   THEN 5
               WHEN '[FAKE] Sunset Ponta d''Areia' THEN 3
               WHEN '[FAKE] Roda de Samba'        THEN 2
               ELSE 0
           END AS qtd
    FROM eventos e WHERE e.title LIKE '[FAKE]%'
) x
JOIN LATERAL (
    SELECT id FROM users WHERE email LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT x.qtd
) u ON true;

-- ---------------------------------------------------------------------------
-- 6. Interesse ("Bora!") nos eventos futuros — alimenta o contador do card e a
--    lista de confirmados no detalhe.
-- ---------------------------------------------------------------------------
INSERT INTO eventos_confirmacoes (evento_id, user_id)
SELECT e.id, u.id
FROM eventos e
CROSS JOIN users u
WHERE e.title LIKE '[FAKE]%'
  AND e.start_date > now()
  AND u.email LIKE 'fake+%@spotrole.dev'
  -- espalhamento estável: cada evento fica com um punhado diferente de interessados
  AND ((e.id * 7 + u.id) % 13) < (2 + (e.id % 7))
ON CONFLICT DO NOTHING;

-- ---------------------------------------------------------------------------
-- 7. Histórico e avaliações dos eventos encerrados
-- ---------------------------------------------------------------------------
INSERT INTO participacao_evento (user_id, evento_id, check_in_at, origem)
SELECT u.id, e.id, e.start_date + interval '30 minutes', 'GEOFENCE'
FROM eventos e
JOIN LATERAL (SELECT id FROM users WHERE email LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT 6) u ON true
WHERE e.title IN ('[FAKE] Aniversário do Bar X', '[FAKE] Esquenta de Carnaval', '[FAKE] Baile Charme');

INSERT INTO avaliacao (nota, comentario, user_id, evento_id, created_at)
SELECT
    (ARRAY[5,4,5,3,4])[1 + (u.rn % 5)],
    (ARRAY[
        'Melhor rolê do mês, sem discussão.',
        'Curti bastante, só faltou mais opção de comida.',
        'Som impecável e o pessoal super de boa.',
        'Lotou demais, quase não deu pra andar.',
        'Voltaria com certeza. Preço justo.'
    ])[1 + (u.rn % 5)],
    u.id, e.id, e.end_date + interval '1 day'
FROM eventos e
JOIN LATERAL (
    SELECT id, row_number() OVER (ORDER BY id) AS rn
    FROM users WHERE email LIKE 'fake+%@spotrole.dev' LIMIT 5
) u ON true
WHERE e.title IN ('[FAKE] Aniversário do Bar X', '[FAKE] Baile Charme');

-- ---------------------------------------------------------------------------
-- 8. Vincula a SUA conta, sem precisar editar nada: pega o primeiro usuário que
--    NÃO é fake, ou seja, a conta que você registrou no app. Se ainda não houver
--    nenhuma, este bloco simplesmente não faz nada.
--      - interesse em 3 eventos futuros  -> aparece na Agenda
--      - participação num evento passado -> libera avaliar e entra no histórico
--      - um evento passado COM interesse e SEM participação -> cai no card
--        "você foi ao evento?" (GET /participacao/pendentes)
-- ---------------------------------------------------------------------------
INSERT INTO eventos_confirmacoes (evento_id, user_id)
SELECT e.id, (SELECT id FROM users WHERE email NOT LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT 1)
FROM eventos e
WHERE e.title IN ('[FAKE] Show de Reggae', '[FAKE] Praia Party', '[FAKE] Roda de Samba',
                  '[FAKE] Esquenta de Carnaval')
  AND EXISTS (SELECT 1 FROM users WHERE email NOT LIKE 'fake+%@spotrole.dev')
ON CONFLICT DO NOTHING;

INSERT INTO participacao_evento (user_id, evento_id, check_in_at, origem)
SELECT (SELECT id FROM users WHERE email NOT LIKE 'fake+%@spotrole.dev' ORDER BY id LIMIT 1),
       e.id, e.start_date + interval '20 minutes', 'GEOFENCE'
FROM eventos e
WHERE e.title = '[FAKE] Aniversário do Bar X'
  AND EXISTS (SELECT 1 FROM users WHERE email NOT LIKE 'fake+%@spotrole.dev');

COMMIT;

-- ============================================================================
--  Conferência: o que cada aba do app deve mostrar agora.
--  SEMANA inclui os de HOJE e MES inclui os de SEMANA — é assim no backend.
-- ============================================================================
SELECT 'AGORA'  AS aba, count(*) AS eventos FROM eventos
  WHERE title LIKE '[FAKE]%' AND start_date BETWEEN now() - interval '3 hours' AND now() + interval '6 hours'
UNION ALL SELECT 'HOJE',   count(*) FROM eventos
  WHERE title LIKE '[FAKE]%' AND start_date BETWEEN now() AND date_trunc('day', now()) + interval '1 day' - interval '1 second'
UNION ALL SELECT 'SEMANA', count(*) FROM eventos
  WHERE title LIKE '[FAKE]%' AND start_date BETWEEN now() AND now() + interval '7 days'
UNION ALL SELECT 'MES',    count(*) FROM eventos
  WHERE title LIKE '[FAKE]%' AND start_date BETWEEN now() AND now() + interval '1 month'
UNION ALL SELECT 'encerrados (histórico)', count(*) FROM eventos
  WHERE title LIKE '[FAKE]%' AND estado_evento = 'ENCERRADO'
UNION ALL SELECT 'check-ins ativos (heatmap)', count(*) FROM tb_presenca_ativa
  WHERE evento_id IN (SELECT id FROM eventos WHERE title LIKE '[FAKE]%')
UNION ALL SELECT 'usuários fake', count(*) FROM users WHERE email LIKE 'fake+%@spotrole.dev';
