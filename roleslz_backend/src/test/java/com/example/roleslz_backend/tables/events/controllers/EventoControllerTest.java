package com.example.roleslz_backend.tables.events.controllers;

import com.example.roleslz_backend.Tables.events.DTO.*;
import com.example.roleslz_backend.Tables.events.controllers.EventoController;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.services.EventoService;
import com.example.roleslz_backend.Tables.users.DTOS.UserDTODetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void createEventoSuccess() throws Exception {
        EventDTORequestClass requestDTO = new EventDTORequestClass("Show do Bita", "Show infantil", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 50.0f);
        EventDTOClass responseDTO = new EventDTOClass(1L, "Show do Bita", "Show infantil", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 50.0f, null, null);

        when(eventoService.createEvento(any(EventDTORequestClass.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/evento/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Show do Bita"));

        verify(eventoService, times(1)).createEvento(any(EventDTORequestClass.class));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e eventos ao buscar por raio")
    void getEventsAroundSuccess() throws Exception {
        GetEventsAroundDTO requestDTO = new GetEventsAroundDTO(-2.53, -44.30, 5.0);
        Set<EventoDTOResponseDistance> responseSet = new HashSet<>();
        responseSet.add(new EventoDTOResponseDistance(1L, "Evento 1", "Desc 1", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 50.0f, 1.5));

        when(eventoService.getEventsAround(anyDouble(), anyDouble(), anyDouble())).thenReturn(responseSet);

        mockMvc.perform(get("/evento/get_events_around")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Evento 1"));

        verify(eventoService, times(1)).getEventsAround(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e eventos ao buscar por área de mapa")
    void getEventsInMapAreaSuccess() throws Exception {
        GetsEventsInMapAreaDTO requestDTO = new GetsEventsInMapAreaDTO(-3.0, -45.0, -2.0, -44.0);
        Set<EventoDTOResponseDistance> responseSet = new HashSet<>();
        responseSet.add(new EventoDTOResponseDistance(1L, "Evento Mapa", "Desc Mapa", -2.5, -44.5, "Rua B", "14:00", "16:00", "2024-12-26", 0.0f, 0.0));

        when(eventoService.getEventosInMapArea(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(responseSet);

        mockMvc.perform(get("/evento/get_events_in_map_area")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Evento Mapa"));

        verify(eventoService, times(1)).getEventosInMapArea(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o evento ao buscar por ID")
    void getEventByIdSuccess() throws Exception {
        long eventId = 1L;
        EventDTOClass responseDTO = new EventDTOClass(eventId, "Show do Bita", "Show infantil", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 50.0f, null, null);

        when(eventoService.getEvento(eventId)).thenReturn(responseDTO);

        mockMvc.perform(get("/evento/get/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Show do Bita"));

        verify(eventoService, times(1)).getEvento(eventId);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar evento inexistente por ID")
    void getEventByIdNotFound() throws Exception {
        long eventId = 99L;
        when(eventoService.getEvento(eventId)).thenThrow(new EventNotFounded("Evento não encontrado"));

        mockMvc.perform(get("/evento/get/{id}", eventId))
                .andExpect(status().isNotFound()); // Assumindo que um ControllerAdvice converteria EventoDoesntExists para 404

        verify(eventoService, times(1)).getEvento(eventId);
    }

    @Test
    @DisplayName("Deve editar um evento com sucesso e retornar 200 OK")
    void editEventSuccess() throws Exception {
        long eventId = 1L;
        EventDTORequestClass requestDTO = new EventDTORequestClass("Show do Bita Editado", "Show infantil", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 60.0f);
        EventDTOClass responseDTO = new EventDTOClass(eventId, "Show do Bita Editado", "Show infantil", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 60.0f, null, null);

        when(eventoService.editEvento(anyLong(), any(EventDTORequestClass.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/evento/edit/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Show do Bita Editado"));

        verify(eventoService, times(1)).editEvento(anyLong(), any(EventDTORequestClass.class));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar editar evento inexistente")
    void editEventNotFound() throws Exception {
        long eventId = 99L;
        EventDTORequestClass requestDTO = new EventDTORequestClass("Show do Bita Editado", "Show infantil", -2.53, -44.30, "Rua A", "10:00", "12:00", "2024-12-25", 60.0f);

        when(eventoService.editEvento(anyLong(), any(EventDTORequestClass.class))).thenThrow(new EventNotFounded("Evento não encontrado para edição"));

        mockMvc.perform(patch("/evento/edit/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(eventoService, times(1)).editEvento(anyLong(), any(EventDTORequestClass.class));
    }

    @Test
    @DisplayName("Deve editar o preço de um evento com sucesso e retornar 200 OK")
    void editPriceSuccess() throws Exception {
        long eventId = 1L;
        NewPriceDTO requestDTO = new NewPriceDTO(75.0f);

        doNothing().when(eventoService).editPrice(anyLong(), any(NewPriceDTO.class));

        mockMvc.perform(patch("/evento/edit_price/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newPrice").value(75.0f)); // Retorna o DTO de entrada

        verify(eventoService, times(1)).editPrice(anyLong(), any(NewPriceDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar editar preço de evento inexistente")
    void editPriceNotFound() throws Exception {
        long eventId = 99L;
        NewPriceDTO requestDTO = new NewPriceDTO(75.0f);

        doThrow(new EventNotFounded("Evento não encontrado para editar preço")).when(eventoService).editPrice(anyLong(), any(NewPriceDTO.class));

        mockMvc.perform(patch("/evento/edit_price/{id}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());

        verify(eventoService, times(1)).editPrice(anyLong(), any(NewPriceDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de confirmados")
    void getConfirmsSuccess() throws Exception {
        long eventId = 1L;
        Set<UserDTODetails> confirms = new HashSet<>();
        confirms.add(new UserDTODetails(1L, "User 1", "user1@example.com"));
        confirms.add(new UserDTODetails(2L, "User 2", "user2@example.com"));

        when(eventoService.getConfirms(eventId)).thenReturn(confirms);

        mockMvc.perform(get("/evento/get_confirms/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").exists());

        verify(eventoService, times(1)).getConfirms(eventId);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao buscar confirmados de evento inexistente")
    void getConfirmsNotFound() throws Exception {
        long eventId = 99L;
        when(eventoService.getConfirms(eventId)).thenThrow(new EventNotFounded("Evento não encontrado para buscar confirmados"));

        mockMvc.perform(get("/evento/get_confirms/{id}", eventId))
                .andExpect(status().isNotFound());

        verify(eventoService, times(1)).getConfirms(eventId);
    }

    @Test
    @DisplayName("Deve remover confirmação com sucesso e retornar 202 Accepted")
    void removeConfirmSuccess() throws Exception {
        long eventId = 1L;
        long userId = 1L;

        doNothing().when(eventoService).removeConfirms(eventId, userId);

        mockMvc.perform(patch("/evento/remove_confirm/{event_id}/user/{user_id}", eventId, userId))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Confirmação removida"));

        verify(eventoService, times(1)).removeConfirms(eventId, userId);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar remover confirmação de evento inexistente")
    void removeConfirmEventNotFound() throws Exception {
        long eventId = 99L;
        long userId = 1L;

        doThrow(new EventNotFounded("Evento não encontrado para remover confirmação")).when(eventoService).removeConfirms(eventId, userId);

        mockMvc.perform(patch("/evento/remove_confirm/{event_id}/user/{user_id}", eventId, userId))
                .andExpect(status().isNotFound());

        verify(eventoService, times(1)).removeConfirms(eventId, userId);
    }

    @Test
    @DisplayName("Deve deletar um evento com sucesso e retornar 200 OK")
    void deleteEventSuccess() throws Exception {
        long eventId = 1L;

        doNothing().when(eventoService).deleteEvento(eventId);

        mockMvc.perform(delete("/evento/delete/{id}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().string("Evento deletado"));

        verify(eventoService, times(1)).deleteEvento(eventId);
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found ao tentar deletar evento inexistente")
    void deleteEventNotFound() throws Exception {
        long eventId = 99L;

        doThrow(new EventNotFounded("Evento não encontrado para deleção")).when(eventoService).deleteEvento(eventId);

        mockMvc.perform(delete("/evento/delete/{id}", eventId))
                .andExpect(status().isNotFound());

        verify(eventoService, times(1)).deleteEvento(eventId);
    }
}
