package com.example.roleslz_backend.Utills.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Você pode definir uma URL base aqui se for usar apenas para essa API
        return builder.baseUrl("https://receitaws.com.br/v1/cnpj/").build();
    }
}