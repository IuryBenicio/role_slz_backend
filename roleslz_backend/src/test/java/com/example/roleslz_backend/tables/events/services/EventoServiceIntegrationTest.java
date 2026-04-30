package com.example.roleslz_backend.tables.events.services;

import com.example.roleslz_backend.Tables.categoria.entity.CategoriaEntity;
import com.example.roleslz_backend.Tables.categoria.exceptions.CategoryNotFounded;
import com.example.roleslz_backend.Tables.categoria.repository.CategoriaRepository;
import com.example.roleslz_backend.Tables.events.entity.EventoEntity;
import com.example.roleslz_backend.Tables.events.exceptions.EventNotFounded;
import com.example.roleslz_backend.Tables.events.repository.EventoRepository;
import com.example.roleslz_backend.Tables.events.services.EventoService;
import com.example.roleslz_backend.Tables.spot.entity.SpotEntity;
import com.example.roleslz_backend.Tables.spot.repository.SpotRepository;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventoServiceIntegrationTest {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpotRepository spotRepository;

    private EventoEntity evento;
    private CategoriaEntity categoria;
    private UserEntity organizador;

    private final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void setUp() {
        // Limpar dados para garantir isolamento (ordem reversa de dependência)
        eventoRepository.deleteAll();
        spotRepository.deleteAll();
        categoriaRepository.deleteAll();
        userRepository.deleteAll();

        // Criar e salvar organizador (dependência do evento)
        organizador = new UserEntity();
        organizador.setNome("Organizador Teste");
        organizador.setSobrenome("Sobrenome");
        organizador.setEmail("organizador@teste.com");
        organizador.setPassword("senha123");
        organizador.setIdade(30);
        userRepository.save(organizador);

        // Criar e salvar spot (dependência do evento)
        SpotEntity spot = new SpotEntity();
        Point location = factory.createPoint(new Coordinate(-44.30, -2.53));
        spot.setLocalizacao(location);
        spotRepository.save(spot);

        // Criar e salvar evento principal do teste
        evento = new EventoEntity();
        evento.setTitle("Evento de Teste");
        evento.setStartDate(LocalDateTime.now().plusDays(1));
        evento.setEndDate(LocalDateTime.now().plusDays(1).plusHours(2));
        evento.setEnderecoExtenso("Rua de Teste, 123");
        evento.setOrganizador(organizador);
        evento.setSpot(spot); // CORREÇÃO: Associando o spot obrigatório
        evento.setCategorias(new HashSet<>());
        eventoRepository.save(evento);

        // Criar e salvar categoria para os testes
        categoria = new CategoriaEntity();
        categoria.setName("Show");
        categoriaRepository.save(categoria);
    }

    @Test
    @DisplayName("Deve adicionar uma categoria a um evento com sucesso")
    void addCategoria_Success() {
        // Act
        eventoService.addCategoria(evento.getId(), categoria.getName());

        // Assert
        EventoEntity eventoAtualizado = eventoRepository.findById(evento.getId()).orElseThrow();
        assertThat(eventoAtualizado.getCategorias()).hasSize(1);
        assertThat(eventoAtualizado.getCategorias().iterator().next().getName()).isEqualTo("Show");
    }

    @Test
    @DisplayName("Deve lançar EventNotFounded ao tentar adicionar categoria a um evento inexistente")
    void addCategoria_EventNotFound() {
        // Arrange
        long idEventoInexistente = 999L;

        // Act & Assert
        assertThatThrownBy(() -> eventoService.addCategoria(idEventoInexistente, categoria.getName()))
                .isInstanceOf(EventNotFounded.class)
                .hasMessage("Evento não encontrado");
    }

    @Test
    @DisplayName("Deve lançar CategoryNotFounded ao tentar adicionar uma categoria inexistente a um evento")
    void addCategoria_CategoryNotFound() {
        // Arrange
        String categoriaInexistente = "Inexistente";

        // Act & Assert
        assertThatThrownBy(() -> eventoService.addCategoria(evento.getId(), categoriaInexistente))
                .isInstanceOf(CategoryNotFounded.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    @DisplayName("Não deve adicionar categoria duplicada ao evento")
    void addCategoria_ShouldNotAddDuplicateCategory() {
        // Arrange: Adiciona a categoria uma vez para o estado inicial do teste
        eventoService.addCategoria(evento.getId(), categoria.getName());
        EventoEntity eventoAposPrimeiraAdicao = eventoRepository.findById(evento.getId()).get();
        assertThat(eventoAposPrimeiraAdicao.getCategorias()).hasSize(1);

        // Act: Tenta adicionar a mesma categoria novamente.
        // A implementação do Set em Java já previne duplicatas, então o método deve executar sem erro.
        eventoService.addCategoria(evento.getId(), categoria.getName());

        // Assert: Verifica se o tamanho da coleção de categorias não mudou.
        EventoEntity eventoAposSegundaAdicao = eventoRepository.findById(evento.getId()).get();
        assertThat(eventoAposSegundaAdicao.getCategorias()).hasSize(1);
    }
}
