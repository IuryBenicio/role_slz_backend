package com.example.roleslz_backend.Tables.verificarEmail.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // CRIO O EMAIL PARA VERIFICAÇÃO
    public void sendVerificationEmail(String to, String token){
        //Será como uma requisição feita como resposta ao servidor
        String link = "http://{APP_HOST_LINK}:{APP_PORT}/auth/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirme seu cadastro - Rolê SLZ");
        message.setText("Olá! Clique no link para ativar sua conta: " + link);

        javaMailSender.send(message);
    }
}
