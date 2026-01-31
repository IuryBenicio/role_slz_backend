package com.example.roleslz_backend.Utills.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FireBaseConfiguration{

    @PostConstruct
    public void itialize(){
        try{
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/firebasekey.json");

            FirebaseOptions options = FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();


        } catch (IOException e) {
            System.err.println("Firebase não inicializado: " + e);
        }
    }

}
