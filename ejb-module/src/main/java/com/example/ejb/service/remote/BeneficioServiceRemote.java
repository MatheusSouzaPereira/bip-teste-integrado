package com.example.ejb.service.remote;

import com.example.ejb.entity.Beneficio;

import javax.ejb.Remote;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Remote
public interface BeneficioServiceRemote {
    Beneficio criar(String nome, String descricao, BigDecimal valor);
    Optional<Beneficio> buscarPorId(Long id);
    List<Beneficio> listarTodos();
    Beneficio atualizar(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo);
    void remover(Long id);
}
