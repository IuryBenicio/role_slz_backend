package com.example.roleslz_backend.Tables.enterprises.entity;

import com.example.roleslz_backend.Utills.BaseEntity.BaseEntity;
import com.example.roleslz_backend.Utills.FuncionamentoClass;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "empresa")
public class EmpresaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "CNPJ")
    @NotBlank(message = "CPNJ precisa ser preenchido")
    private String cnpj;

    @Column(name = "nome_fantasia")
    @NotBlank(message = "Nome fantasia precisa ser preenchido")
    private String nomeFantasia;

    @Column(name = "logoTipoUrl")
    @NotBlank(message = "Logo tipo precisa ter URL")
    private String logoTipoUrl;

    @Embedded
    private FuncionamentoClass funcionamento;

}
