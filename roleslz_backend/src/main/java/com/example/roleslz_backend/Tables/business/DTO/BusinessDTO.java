package com.example.roleslz_backend.Tables.business.DTO;

import com.example.roleslz_backend.Utills.FuncionamentoClass;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;

public record BusinessDTO(
        @NotBlank String cnpj,
        @NotBlank String nomeFantasia,
        @NotBlank String logoTipoUrl,
        @Embedded FuncionamentoClass funcionamentoClass
        ) {
}
