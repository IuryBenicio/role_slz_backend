package com.example.roleslz_backend.Utills.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FireBaseConfiguration {

    /**
     * Caminho (no classpath) do service account do Firebase.
     * Em produção/dev fica em src/main/resources/firebasekey.json (NÃO versionado);
     * nos testes um arquivo fake em src/test/resources/firebasekey.json tem precedência.
     */
    @Value("${firebase.credentials.classpath:firebasekey.json}")
    private String credentialsClasspath;

    // destroyMethod = "delete": encerra o pool de threads do Firebase Admin SDK no shutdown
    // do contexto (sem isso a JVM — inclusive a de testes — fica presa ~30s ao sair).
    @Bean(destroyMethod = "delete")
    public FirebaseApp firebaseApp() throws IOException {
        // Idempotente: evita "FirebaseApp name [DEFAULT] already exists" em recarregamentos de contexto.
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try (InputStream serviceAccount = new ClassPathResource(credentialsClasspath).getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase inicializado com sucesso (credenciais: {}).", credentialsClasspath);
            return app;
        } catch (IOException e) {
            log.error("Erro crítico ao inicializar o Firebase: '{}' não encontrado ou inválido no classpath.",
                    credentialsClasspath, e);
            throw e;
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
