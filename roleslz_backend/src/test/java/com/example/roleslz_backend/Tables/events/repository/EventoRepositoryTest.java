package com.example.roleslz_backend.Tables.events.repository;

import com.example.roleslz_backend.Tables.events.Projections.EventoComDistanciaProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests das queries nativas (PostGIS) do módulo de eventos.
 * Sobe um PostGIS real no Docker via Testcontainers — não roda sem o Docker ligado.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // NÃO troca o PostGIS por um banco em memória
class EventoRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:15-3.3")
                    .asCompatibleSubstituteFor("postgres")
    )
            .withDatabaseName("spotrole_test")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private PresencaAtivaRepository presencaAtivaRepository;

    // Bounding Box fictícia de São Luís, reutilizada pelos testes
    private static final double MIN_LON = -44.3200;
    private static final double MIN_LAT = -2.5500;
    private static final double MAX_LON = -44.2800;
    private static final double MAX_LAT = -2.4900;

    @Test
    @DisplayName("Deve executar a query do Mapa de Calor sem erros de sintaxe ou parâmetros do Hibernate")
    void deveExecutarQueryDoHeatmapComSucesso() {
        // A agregação do heatmap vive em PresencaAtivaRepository.
        // Se a query nativa tiver algum erro de parâmetro/sintaxe, o teste falha aqui na hora!
        assertDoesNotThrow(
                () -> presencaAtivaRepository.findHeatmapData(MIN_LON, MIN_LAT, MAX_LON, MAX_LAT),
                "A query do Heatmap falhou ao ser executada pelo Hibernate!"
        );
    }

    @Test
    @DisplayName("Deve executar a query de Buscar Eventos em uma Área (Feed do Mapa) sem erros")
    void deveExecutarQueryBuscarEventosEmUmaAreaComSucesso() {
        double centerLng = -44.3000;
        double centerLat = -2.5200;

        List<EventoComDistanciaProjection> resultado = assertDoesNotThrow(
                () -> eventoRepository.buscarEventosEmUmaArea(
                        centerLng, centerLat, MIN_LON, MIN_LAT, MAX_LON, MAX_LAT
                ),
                "A query principal do Mapa falhou ao ser executada pelo Hibernate!"
        );

        // Banco vazio: só garantimos que a query nativa de Geofencing roda e devolve uma lista.
        assertThat(resultado).isNotNull();
    }
}
