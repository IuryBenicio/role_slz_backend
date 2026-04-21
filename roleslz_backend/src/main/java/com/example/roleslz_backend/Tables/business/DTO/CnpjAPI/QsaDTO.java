package com.example.roleslz_backend.Tables.business.DTO.CnpjAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class QsaDTO {
    @JsonProperty("nome_socio")
    private String nomeSocio;
    @JsonProperty("qualificacao_socio")
    private String qualificacaoSocio;
}
