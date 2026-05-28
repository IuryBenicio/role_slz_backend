package com.example.roleslz_backend.Tables.users.services;

import com.example.roleslz_backend.Tables.users.DTOS.Req.PasswordDTOForgot;
import com.example.roleslz_backend.Tables.users.DTOS.Req.PasswordDTOReset;
import com.example.roleslz_backend.Tables.verificarEmail.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("auth")
public class PasswordController {

    final private PasswordResetService redisService; // Aquele que criamos para o Redis
    final private EmailService emailService; // Seu serviço de e-mail já existente
    final private UserService userService; // Para atualizar a senha no banco principal

    public PasswordController(PasswordResetService redisService, EmailService emailService, UserService userService) {
        this.redisService = redisService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid PasswordDTOForgot dto) {
        String codigo = String.format("%06d", new Random().nextInt(999999));

        redisService.salvarCodigo(dto.email(), codigo);

        emailService.sendVerficationAccountEmail(dto.email(), codigo);

        return ResponseEntity.ok(Map.of("message", "Se o e-mail existir, um código será enviado."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid PasswordDTOReset dto) {
        boolean isValid = redisService.validarCodigo(dto.email(), dto.codigo());

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Código inválido ou expirado."));
        }

        userService.editPasswordByEmail(dto.email(), dto.novaSenha());

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso! Você já pode fazer login."));
    }
}
