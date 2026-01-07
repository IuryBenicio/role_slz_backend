package com.example.roleslz_backend.Tables.VerificarEmail.entity;

import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
public class EmailEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id")
        private Long id;

        @Column(name = "token")
        private String token;

        @Column(name = "expires_at")
        private LocalDateTime expiresAt;

        @Column(name = "used")
        private boolean used = false;

        @OneToOne
        @JoinColumn(name = "user_id")
        private UserEntity usuario;

}
