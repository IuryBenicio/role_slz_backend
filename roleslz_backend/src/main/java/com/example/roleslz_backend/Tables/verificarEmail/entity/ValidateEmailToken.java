package com.example.roleslz_backend.Tables.verificarEmail.entity;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ValidateEmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    private UserEntity user;
    private LocalDateTime expiryDate;

}
