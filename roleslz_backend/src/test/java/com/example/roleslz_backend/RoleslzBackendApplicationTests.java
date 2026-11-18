package com.example.roleslz_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Smoke test: garante que o ApplicationContext inteiro sobe — inclui a fiação do Firebase
 * (FirebaseApp + FirebaseMessaging), JPA/PostGIS, schedulers, security e serviços de notificação.
 * Precisa do Docker ligado (Testcontainers sobe um PostGIS).
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
// Fecha o contexto ao fim da classe: os pools (Firebase, Lettuce, schedulers) são encerrados
// dentro do ciclo do teste, e não como threads penduradas depois do System.exit().
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RoleslzBackendApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:15-3.3")
                    .asCompatibleSubstituteFor("postgres")
    )
            .withDatabaseName("spotrole_test")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void contextLoads() {
    }
}
