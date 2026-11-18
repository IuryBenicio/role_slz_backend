package com.example.roleslz_backend.infra.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.infra.security.ErrorToGenerateToken;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class SecurityService {

    // ATENÇÃO: o `$` aqui não é decorativo. Sem ele o Spring injeta a string literal
    // "{api.security.token.secret}" como chave HMAC — ou seja, todo token do sistema
    // passa a ser assinado com um segredo público e qualquer um consegue forjar a
    // identidade de qualquer usuário. Sem valor default de propósito: se a propriedade
    // faltar, a aplicação não sobe.
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-days:30}")
    private long expirationDays;

    @PostConstruct
    void validarSegredo() {
        if (secret == null || secret.isBlank() || secret.startsWith("{")) {
            throw new IllegalStateException(
                    "api.security.token.secret não foi resolvido. Confira a propriedade e a sintaxe ${...} no @Value.");
        }
    }

    public String generateToken(UserEntity user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("spot_slz")
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
        }catch(JWTCreationException ex){
            throw new ErrorToGenerateToken("Erro ao criar token!" + ex);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("spot_slz")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

    // Instant é absoluto: não depende de fuso. A versão anterior somava um ano e
    // convertia com ZoneOffset.of("-05:00") — offset que não é nem o de São Luís (-03:00).
    private Instant genExpirationDate(){
        return Instant.now().plus(Duration.ofDays(expirationDays));
    }
}
