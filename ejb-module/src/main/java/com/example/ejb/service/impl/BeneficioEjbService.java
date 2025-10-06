package com.example.ejb.service.impl;

import com.example.ejb.entity.Beneficio;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext(unitName = "bipPU")
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs de origem e destino são obrigatórios");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Origem e destino devem ser diferentes");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio to   = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);

        if (from == null) {
            throw new IllegalArgumentException("Benefício origem não encontrado: id=" + fromId);
        }
        if (to == null) {
            throw new IllegalArgumentException("Benefício destino não encontrado: id=" + toId);
        }
        if (Boolean.FALSE.equals(from.getAtivo())) {
            throw new IllegalStateException("Benefício origem está inativo");
        }
        if (Boolean.FALSE.equals(to.getAtivo())) {
            throw new IllegalStateException("Benefício destino está inativo");
        }

        BigDecimal saldoOrigem = from.getValor();
        if (saldoOrigem == null) {
            throw new IllegalStateException("Saldo de origem inválido (nulo)");
        }
        if (saldoOrigem.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente para transferência");
        }

        from.setValor(saldoOrigem.subtract(amount));
        to.setValor(to.getValor().add(amount));

        em.merge(from);
        em.merge(to);
        em.flush();
    }
}
