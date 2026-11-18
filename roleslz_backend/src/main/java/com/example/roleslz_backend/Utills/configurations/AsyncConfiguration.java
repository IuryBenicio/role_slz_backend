package com.example.roleslz_backend.Utills.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Pool dedicado para o disparo de push.
 *
 * <p>São threads de plataforma de propósito, não virtuais. O envio ao Firebase é
 * I/O bloqueante e sem limite natural: com virtual threads o app aceitaria criar
 * milhares de envios simultâneos e quem apanharia seria o Firebase (e a cota).
 * Um pool pequeno com fila limitada funciona como represa — e quando a fila enche,
 * CallerRunsPolicy faz quem produziu o push executá-lo, o que naturalmente segura
 * o produtor em vez de acumular trabalho na memória até estourar.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    public static final String PUSH_EXECUTOR = "pushExecutor";

    @Bean(PUSH_EXECUTOR)
    public Executor pushExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("push-fcm-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // No shutdown, termina o que já está na fila em vez de descartar notificação.
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(20);
        executor.initialize();
        return executor;
    }
}
