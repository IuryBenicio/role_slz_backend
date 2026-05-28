package com.example.roleslz_backend.Utills.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FireBaseConfiguration {

    @PostConstruct
    public void initialize() {
        try {
            // Carregamento seguro para JAR/Produção usando ClassPathResource
            InputStream serviceAccount = new ClassPathResource("firebasekey.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Verifica se já não existe uma instância iniciada (evita erro de App já existente)
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.getLogger("Firebase inicializado com sucesso!");
            }

        } catch (IOException e) {
            System.getLogger("❌ Erro crítico: Arquivo firebasekey.json não encontrado ou inválido!");
            e.printStackTrace();
        }
    }
}