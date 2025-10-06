package com.example.backend.beneficio.service.impl;

import com.example.backend.beneficio.entity.Beneficio;
import com.example.backend.beneficio.repository.BeneficioRepository;
import com.example.backend.beneficio.service.BeneficioService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Profile("!ejb")
public class BeneficioServiceImpl implements BeneficioService {
    private final BeneficioRepository repository;

    @PersistenceContext
    private EntityManager em;

    public BeneficioServiceImpl(BeneficioRepository repository) {
        this.repository = repository;
    }

    public List<Beneficio> list() {
        return repository.findAll();
    }
    public Beneficio findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Benefício não encontrado"));
    }
    public Beneficio create(Beneficio b) {
        return repository.save(b);
    }

    public Beneficio update(Long id, Beneficio b) {
        Beneficio existing = findById(id);
        existing.setNome(b.getNome());
        existing.setDescricao(b.getDescricao());
        existing.setValor(b.getValor());
        existing.setAtivo(b.getAtivo());
        return repository.save(existing);
    }
    public void deleted(Long id) { repository.deleteById(id); }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        validateFromTo(fromId.equals(toId), "Origem e destino devem ser diferentes");
        validateFromTo(amount == null || amount.signum() <= 0, "Valor deve ser positivo");

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        Beneficio to   = em.find(Beneficio.class, toId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        if (from == null || to == null) throw new EntityNotFoundException("Benefício de origem ou destino não encontrado");
        if (Boolean.FALSE.equals(from.getAtivo()) || Boolean.FALSE.equals(to.getAtivo())) throw new IllegalStateException("Benefício inativo");
        BigDecimal fromSaldo = from.getValor() == null ? BigDecimal.ZERO : from.getValor();
        if (fromSaldo.compareTo(amount) < 0) throw new IllegalStateException("Saldo insuficiente");

        from.setValor(fromSaldo.subtract(amount));
        to.setValor(to.getValor() == null ? amount : to.getValor().add(amount));
    }

    private static void validateFromTo(boolean fromId, String s) {
        if (fromId) throw new IllegalArgumentException(s);
    }

}
