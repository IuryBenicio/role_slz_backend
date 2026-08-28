ALTER TABLE avaliacao ADD COLUMN comentario TEXT;
-- 1 avaliação por pessoa por evento
ALTER TABLE avaliacao ADD CONSTRAINT uq_avaliacao_user_evento UNIQUE (user_id, evento_id);
