package com.example.roleslz_backend.Tables.business.DTO.CnpjAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class RegimeTributarioDTO {
    private Integer ano;
    @JsonProperty("forma_de_tributacao")
    private String formaDeTributacao;
}
