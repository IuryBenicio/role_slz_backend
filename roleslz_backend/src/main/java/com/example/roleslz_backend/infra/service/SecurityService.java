package com.example.roleslz_backend.infra.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import com.example.roleslz_backend.infra.security.ErrorToGenerateToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class SecurityService {
    @Value("{api.security.token.secret}")
    private String secret;

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

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusYears(1).toInstant(ZoneOffset.of("-05:00"));
    }
}
