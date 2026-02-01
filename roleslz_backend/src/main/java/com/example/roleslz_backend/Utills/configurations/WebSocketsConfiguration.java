package com.example.roleslz_backend.Utills.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketsConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        //agendador de tarefas para os batimentos
        ThreadPoolTaskScheduler te = new ThreadPoolTaskScheduler();
        te.setPoolSize(1);
        te.setThreadNamePrefix("ws-heartbeat-");
        te.initialize();

        // Habilita um broker simples na memória para enviar mensagens aos clientes
        config.enableSimpleBroker("/topic")
                // Primeiro valor: quanto o servidor envia para mostrar que está ativo (10s)
                // Segundo valor: quanto o servidor espera receber a resposta do dispositívo (10s)
                .setHeartbeatValue(new long[]{20000, 20000})
                .setTaskScheduler(te);
        // Prefixo para mensagens que saem do cliente para o servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws-spotrole").setAllowedOrigins("*").withSockJS();
    }
}
