package com.example.roleslz_backend.Tables.verificarEmail.service;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.Tables.users.exceptions.UserNotFounded;
import com.example.roleslz_backend.Tables.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    public EmailService(UserRepository userRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }


    @Value("${spring.mail.username}")
    private String emailRemetente;

    // Injeta o link do ngrok aqui
    @Value("${app.host.link}")
    private String appHostLink;

    @Value("${server.port}")
    private String appPort;


    // CRIO O EMAIL PARA VERIFICAÇÃO
    public void sendVerificationEmail(String to, String token){
        //Será como uma requisição feita como resposta ao servidor
        String link = "http://" + appHostLink + ":" + appPort + "/auth/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailRemetente);
        message.setTo(to);
        message.setSubject("Confirme seu cadastro - Spot Rolê");
        message.setText("Olá! Clique no link para ativar sua conta: " + link);

        javaMailSender.send(message);

    }

    public void sendConfirmationEmail(String to){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailRemetente);
        message.setTo(to);
        message.setSubject("Sua conta foi confirmada - Spot Rolê");
        message.setText("Olá, temos o prazer de confirmar a ativação da sua conta!");

        javaMailSender.send(message);
    }

    public void VerifyTokenEmail(String emailToken){
        UserEntity user = userRepository.findByVerificationToken(emailToken).orElseThrow(()-> new UserNotFounded("Usuário não encontrado na verificação de email"));
        try{
            user.setEnable(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            sendConfirmationEmail(user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("ERRO NA ATIVAÇÃO DA CONTA - erro: " + e);
        }
    }
}
