package com.example.ejb.service;

import com.example.ejb.entity.Beneficio;
import com.example.ejb.service.remote.BeneficioServiceRemote;
import com.example.ejb.service.local.BeneficioServiceLocal;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Stateless
public class BeneficioServiceBean implements BeneficioServiceLocal, BeneficioServiceRemote {

    @PersistenceContext(unitName = "bipPU")
    private EntityManager em;

    @Override
    public Beneficio criar(String nome, String descricao, BigDecimal valor) {
        Beneficio b = new Beneficio();
        b.setNome(nome);
        b.setDescricao(descricao);
        b.setValor(valor);
        b.setAtivo(Boolean.TRUE);
        em.persist(b);
        return b;
    }

    @Override
    public Optional<Beneficio> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Beneficio.class, id));
    }

    @Override
    public List<Beneficio> listarTodos() {
        TypedQuery<Beneficio> q = em.createQuery("select b from Beneficio b", Beneficio.class);
        return q.getResultList();
    }

    @Override
    public Beneficio atualizar(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo) {
        Beneficio b = em.find(Beneficio.class, id);
        if (b == null) {
            throw new IllegalArgumentException("Benefício não encontrado: id=" + id);
        }
        if (nome != null) b.setNome(nome);
        if (descricao != null) b.setDescricao(descricao);
        if (valor != null) b.setValor(valor);
        if (ativo != null) b.setAtivo(ativo);
        return em.merge(b);
    }

    @Override
    public void remover(Long id) {
        Beneficio b = em.find(Beneficio.class, id);
        if (b != null) {
            em.remove(b);
        }
    }
}
