package com.example.ejb;

import com.example.backend.beneficio.entity.Beneficio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BeneficioServiceRemote {
    List<Beneficio> listarTodos();
    Optional<Beneficio> buscarPorId(Long id);
    Beneficio criar(String nome, String descricao, BigDecimal valor);
    Beneficio atualizar(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo);
    void remover(Long id);
}