package com.example.ejb;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class Beneficio implements Serializable {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private Boolean ativo;

}