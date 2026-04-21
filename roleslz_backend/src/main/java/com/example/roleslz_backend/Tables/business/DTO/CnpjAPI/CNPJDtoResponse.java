package com.example.roleslz_backend.Tables.business.DTO.CnpjAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CNPJDtoResponse {
    private String cnpj;
    @JsonProperty("razao_social")
    private String razaoSocial;
    @JsonProperty("nome_fantasia")
    private String nomeFantasia;
    @JsonProperty("descricao_situacao_cadastral")
    private String situacaoCadastral;
    @JsonProperty("data_inicio_atividade")
    private String dataInicioAtividade;
    @JsonProperty("cnae_fiscal_descricao")
    private String cnaeFiscalDescricao;

    // Endereço
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String municipio;
    private String uf;

    // Listas aninhadas
    private List<QsaDTO> qsa;
    @JsonProperty("cnaes_secundarios")
    private List<CnaeSecundarioDTO> cnaesSecundarios;
    @JsonProperty("regime_tributario")
    private List<RegimeTributarioDTO> regimeTributario;

    // Campos adicionais que podem ser úteis para o "Spot Rolê"
    @JsonProperty("ddd_telefone_1")
    private String telefone1;
    private String email;
    @JsonProperty("natureza_juridica")
    private String naturezaJuridica;
}
