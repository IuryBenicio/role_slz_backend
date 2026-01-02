package com.example.roleslz_backend.Utills;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionamentoClass {

    private LocalTime abertura;
    private LocalTime fechamento;
    private String obsercacao;
}
