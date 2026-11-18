package com.example.roleslz_backend.Tables.events.DTO;

/**
 * Corpo do POST /api/checkin/{eventoId}.
 *
 * `entradaEmEpochMs` é o instante em que o APARELHO detectou a entrada na cerca, em
 * milissegundos desde a época (o que `Date.now()` devolve no app). Epoch em vez de
 * data-hora textual de propósito: não há fuso para interpretar errado dos dois lados.
 *
 * O backend usa esse valor para conferir o tempo de permanência (Dwell Time) antes de
 * contar a presença. É um dado do cliente: serve para pegar bug de app, não atacante —
 * a cerca virtual roda no celular de qualquer forma.
 */
public record CheckinRequestDTO(Long entradaEmEpochMs) {
}
