package com.example.roleslz_backend.Utills.notificationsServices;

import java.util.List;
import java.util.Objects;

/**
 * Pacote de push já desligado do banco.
 *
 * <p>Existe por um motivo bem específico: montar a notificação exige ler coleções
 * LAZY da entidade (quem confirmou presença, quem participou), e isso só funciona
 * enquanto a sessão do Hibernate está aberta. Se a gente passasse a entidade para
 * frente e fosse enviar o push depois de a transação fechar, tomaria
 * LazyInitializationException.
 *
 * <p>Então o fluxo é: dentro da transação resolvemos tudo para valores simples e
 * guardamos aqui; fora dela, só resta falar com o Firebase. Como consequência, a
 * conexão com o banco é devolvida ao pool antes da I/O de rede começar.
 */
public record PushJob(
        Long eventoId,
        String estadoEvento,
        String titulo,
        String corpo,
        NotificationsService.TipoNotificacao tipo,
        List<String> tokens
) {

    public PushJob {
        // Normaliza na entrada: sem nulos, sem token repetido (o mesmo usuário pode
        // aparecer em duas coleções) e imutável, porque este objeto atravessa threads.
        tokens = tokens == null
                ? List.of()
                : tokens.stream().filter(Objects::nonNull).distinct().toList();
    }

    public boolean vazio() {
        return tokens.isEmpty();
    }
}
