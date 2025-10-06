package com.example.ejb.service.local;

import com.example.ejb.entity.Beneficio;
import jakarta.ejb.Local;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Local
public interface BeneficioServiceLocal {
    Beneficio criar(String nome, String descricao, BigDecimal valor);
    Optional<Beneficio> buscarPorId(Long id);
    List<Beneficio> listarTodos();
    Beneficio atualizar(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo);
    void remover(Long id);
}