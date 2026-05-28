package com.example.roleslz_backend.Tables.business.services;

import com.example.roleslz_backend.Tables.business.DTO.CnpjAPI.CNPJDtoResponse;
import com.example.roleslz_backend.Tables.business.exceptions.CnpjNaoConfirmado;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CNPJVerificationService {

    private final WebClient webClient;

    public CNPJVerificationService(WebClient webClient) {
        this.webClient = webClient;
    }

    public CNPJDtoResponse buscarCnpj(String cnpj) {
        String cleanCnpj = cnpj.replaceAll("\\D", "");

        return this.webClient.get()
                .uri("/{cnpj}", cleanCnpj) // O Spring concatena com a baseUrl
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        Mono.error(new CnpjNaoConfirmado("CNPJ " + cnpj + " não encontrado."))
                )
                .bodyToMono(CNPJDtoResponse.class)
                .block(); // Bloqueia e retorna o objeto diretamente
    }
}
