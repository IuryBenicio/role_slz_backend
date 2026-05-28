package com.example.roleslz_backend.tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.EventDTOClass;
import com.example.roleslz_backend.Tables.events.DTO.EventDTORequestClass;
import com.example.roleslz_backend.Tables.events.controllers.EventoController;
import com.example.roleslz_backend.Tables.events.exceptions.EventExists;
import com.example.roleslz_backend.Tables.events.services.EventoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventoController.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventoService eventoService;

    @Test
    @DisplayName("Deve criar um evento com sucesso e retornar 200 OK")
    void createEvento_Success() throws Exception {
        // Arrange
        EventDTORequestClass requestDto = new EventDTORequestClass();
        requestDto.setTitle("Show de Rock");
        // Preencha outros campos obrigatórios do DTO de requisição
        requestDto.setLatitude(-2.53);
        requestDto.setLongitude(-44.30);

        EventDTOClass responseDto = new EventDTOClass();
        responseDto.setTitle("Show de Rock");
        responseDto.setPrice(BigDecimal.valueOf(50.00));
        // Preencha outros campos do DTO de resposta

        when(eventoService.createEvento(any(EventDTORequestClass.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/evento/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Show de Rock"))
                .andExpect(jsonPath("$.price").value(50.00));
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando o evento já existe")
    void createEvento_Conflict() throws Exception {
        // Arrange
        EventDTORequestClass requestDto = new EventDTORequestClass();
        requestDto.setTitle("Evento Repetido");
        // Preencha outros campos...

        // Simulamos o serviço lançando a exceção específica
        when(eventoService.createEvento(any(EventDTORequestClass.class)))
                .thenThrow(new EventExists("Evento já existe"));

        // Act & Assert
        mockMvc.perform(post("/evento/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // Assumindo que um @ControllerAdvice mapeia EventExists para 409
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request para uma requisição com corpo inválido")
    void createEvento_BadRequest_InvalidBody() throws Exception {
        // Arrange
        // Enviando um JSON que não corresponde ao DTO (ex: campo 'title' faltando)
        String invalidJson = "{\"description\":\"Evento sem título\"}";

        // Act & Assert
        mockMvc.perform(post("/evento/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve retornar 400 Bad Request para uma requisição com corpo vazio")
    void createEvento_BadRequest_EmptyBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/evento/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // Corpo JSON vazio
                .andExpect(status().isBadRequest()); // Espera-se que a validação falhe
    }

    @Test
    @DisplayName("Deve retornar 500 Internal Server Error para uma exceção inesperada no serviço")
    void createEvento_InternalServerError() throws Exception {
        // Arrange
        EventDTORequestClass requestDto = new EventDTORequestClass();
        requestDto.setTitle("Evento que causa erro");
        // Preencha outros campos...

        // Simulamos o serviço lançando uma exceção genérica
        when(eventoService.createEvento(any(EventDTORequestClass.class)))
                .thenThrow(new RuntimeException("Erro inesperado no banco de dados"));

        // Act & Assert
        mockMvc.perform(post("/evento/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }
}
