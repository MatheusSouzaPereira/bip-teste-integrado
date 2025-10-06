package com.example.backend.beneficio.service.impl;

import com.example.backend.beneficio.entity.Beneficio;
import com.example.backend.beneficio.service.BeneficioService;
import com.example.ejb.BeneficioServiceRemote;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import javax.naming.Context;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Profile("ejb")
public class BeneficioServiceEjbImpl implements BeneficioService {

    private final BeneficioServiceRemote remote;

    public BeneficioServiceEjbImpl(Context context,
                                   @Value("${ejb.beneficio.jndi:ejb:/ejb-module/BeneficioServiceBean!com.example.ejb.BeneficioServiceRemote}") String jndiName) throws NamingException {
        Objects.requireNonNull(context, "InitialContext não pode ser nulo");
        this.remote = (BeneficioServiceRemote) context.lookup(jndiName);
    }

    @Override
    public List<Beneficio> list() {
        return remote.listarTodos().stream().map(BeneficioServiceEjbImpl::mapToBackend).collect(Collectors.toList());
    }

    @Override
    public Beneficio findById(Long id) {
        return remote.buscarPorId(id)
                .map(BeneficioServiceEjbImpl::mapToBackend)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Benefício não encontrado"));
    }

    @Override
    public Beneficio create(Beneficio b) {
        com.example.ejb.Beneficio created = remote.criar(b.getNome(), b.getDescricao(), b.getValor());
        return mapToBackend(created);
    }

    @Override
    public Beneficio update(Long id, Beneficio b) {
        com.example.ejb.Beneficio updated = remote.atualizar(id, b.getNome(), b.getDescricao(), b.getValor(), b.getAtivo());
        return mapToBackend(updated);
    }

    @Override
    public void deleted(Long id) {
        remote.remover(id);
    }

    @Override
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Não há operação equivalente no EJB; podemos simular como débito/crédito em duas chamadas ou expor no EJB futuramente.
        // Por ora, busque e atualize ambos via EJB para manter a consistência simples.
        com.example.ejb.Beneficio from = remote.buscarPorId(fromId).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Origem não encontrada"));
        com.example.ejb.Beneficio to = remote.buscarPorId(toId).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Destino não encontrado"));
        if (Boolean.FALSE.equals(from.getAtivo()) || Boolean.FALSE.equals(to.getAtivo())) throw new IllegalStateException("Benefício inativo");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Valor deve ser positivo");
        java.math.BigDecimal fromSaldo = from.getValor() == null ? java.math.BigDecimal.ZERO : from.getValor();
        if (fromSaldo.compareTo(amount) < 0) throw new IllegalStateException("Saldo insuficiente");
        // efetua transferência simples
        from.setValor(fromSaldo.subtract(amount));
        to.setValor(to.getValor() == null ? amount : to.getValor().add(amount));
        remote.atualizar(from.getId(), from.getNome(), from.getDescricao(), from.getValor(), from.getAtivo());
        remote.atualizar(to.getId(), to.getNome(), to.getDescricao(), to.getValor(), to.getAtivo());
    }

    private static Beneficio mapToBackend(com.example.ejb.Beneficio e) {
        Beneficio b = new Beneficio();
        b.setId(e.getId());
        b.setNome(e.getNome());
        b.setDescricao(e.getDescricao());
        b.setValor(e.getValor());
        b.setAtivo(e.getAtivo());
        return b;
    }
}
