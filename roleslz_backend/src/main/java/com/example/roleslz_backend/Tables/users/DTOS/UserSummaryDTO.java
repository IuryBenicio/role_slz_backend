package com.example.roleslz_backend.Tables.users.DTOS;

import com.example.roleslz_backend.Tables.users.entity.Sexo;
import com.example.roleslz_backend.Tables.users.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;

public record UserSummaryDTO(
                            long Id,
                             String nome,
                             String sobrenome,
                             Sexo sexo,
                            @NotBlank String email,
                             Integer idade,
                             String fcmToken,
                             String image) { // Adicionado

    public static UserSummaryDTO toSummaryDTO(UserEntity entity) {
        if (entity == null) return null;
        return new UserSummaryDTO(
                entity.getId(),
                entity.getNome(),
                entity.getSobrenome(),
                entity.getSexo(),
                entity.getEmail(),
                entity.getIdade(),
                entity.getFcmToken(),
                entity.getImage() // Adicionado
        );
    }
}
