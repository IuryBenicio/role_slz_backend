package com.example.roleslz_backend.Tables.users.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PasswordResetService {
    private StringRedisTemplate redisTemplate;

    public PasswordResetService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String REDIS_PREFIX = "pwd_reset:";

    public void salvarCodigo(String email, String codigo) {
        String chave = REDIS_PREFIX + email;
        redisTemplate.opsForValue().set(chave, codigo, 15, TimeUnit.MINUTES);
    }

    public boolean validarCodigo(String email, String codigoDigitado) {
        String chave = REDIS_PREFIX + email;
        String codigoSalvo = redisTemplate.opsForValue().get(chave);

        if (codigoSalvo != null && codigoSalvo.equals(codigoDigitado)) {
            redisTemplate.delete(chave);
            return true;
        }
        return false;
    }
}
