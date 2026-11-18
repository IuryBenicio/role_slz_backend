-- Registro PERMANENTE de presença num evento (histórico + elegibilidade de avaliação).
-- Diferente de tb_presenca_ativa (cache do heatmap, apagada após o evento), esta tabela nunca é limpa.
CREATE TABLE participacao_evento (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    evento_id    BIGINT NOT NULL REFERENCES eventos(id),
    check_in_at  TIMESTAMP NOT NULL,
    check_out_at TIMESTAMP,
    origem       VARCHAR(20) NOT NULL,
    CONSTRAINT uq_participacao_user_evento UNIQUE (user_id, evento_id)
);

CREATE INDEX idx_participacao_evento_id ON participacao_evento (evento_id);
CREATE INDEX idx_participacao_user_id ON participacao_evento (user_id);
