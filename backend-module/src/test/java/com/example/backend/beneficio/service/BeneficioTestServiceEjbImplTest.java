package com.example.backend.beneficio.service;

import com.example.backend.beneficio.entity.Beneficio;
import com.example.backend.beneficio.service.impl.BeneficioServiceEjbImpl;
import com.example.ejb.BeneficioServiceRemote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.naming.Context;
import javax.naming.NamingException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BeneficioTestServiceEjbImplTest {

    private Context context;
    private BeneficioServiceRemote remote;
    private BeneficioService service;

    @BeforeEach
    void setup() throws NamingException {
        context = mock(Context.class);
        remote = mock(BeneficioServiceRemote.class);
        when(context.lookup(anyString())).thenReturn(remote);
        service = new BeneficioServiceEjbImpl(context, "jndi://dummy");
    }

    private Beneficio ejb(Long id, String nome, BigDecimal valor, boolean ativo) {
        Beneficio b = new Beneficio();
        b.setId(id);
        b.setNome(nome);
        b.setDescricao("desc " + nome);
        b.setValor(valor);
        b.setAtivo(ativo);
        return b;
    }

    @Test
    @DisplayName("list deve mapear resultados do EJB")
    void listMaps() {
        when(remote.listarTodos()).thenReturn(Arrays.asList(
                ejb(1L, "A", new BigDecimal("10.00"), true),
                ejb(2L, "B", new BigDecimal("20.00"), false)
        ));
        var list = service.list();
        assertEquals(2, list.size());
        assertEquals("A", list.get(0).getNome());
        assertEquals(new BigDecimal("20.00"), list.get(1).getValor());
        assertEquals(false, list.get(1).getAtivo());
    }

    @Test
    @DisplayName("findById deve retornar entidade mapeada e lançar quando não encontrado")
    void findById() {
        when(remote.buscarPorId(1L)).thenReturn(Optional.of(ejb(1L, "A", new BigDecimal("10.00"), true)));
        when(remote.buscarPorId(2L)).thenReturn(Optional.empty());

        com.example.backend.beneficio.entity.Beneficio found = service.findById(1L);
        assertEquals("A", found.getNome());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.findById(2L));
    }

    @Test
    @DisplayName("create deve delegar e mapear retorno")
    void create() {
        when(remote.criar(eq("A"), anyString(), eq(new BigDecimal("10.00"))))
                .thenReturn(ejb(10L, "A", new BigDecimal("10.00"), true));
        com.example.backend.beneficio.entity.Beneficio b = new com.example.backend.beneficio.entity.Beneficio();
        b.setNome("A");
        b.setDescricao("d");
        b.setValor(new BigDecimal("10.00"));
        com.example.backend.beneficio.entity.Beneficio created = service.create(b);
        assertEquals(10L, created.getId());
        verify(remote).criar(eq("A"), eq("d"), eq(new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("update deve delegar e mapear retorno")
    void update() {
        when(remote.atualizar(eq(5L), eq("B"), eq("d2"), eq(new BigDecimal("33.00")), eq(Boolean.FALSE)))
                .thenReturn(ejb(5L, "B", new BigDecimal("33.00"), false));
        com.example.backend.beneficio.entity.Beneficio b = new com.example.backend.beneficio.entity.Beneficio();
        b.setNome("B");
        b.setDescricao("d2");
        b.setValor(new BigDecimal("33.00"));
        b.setAtivo(false);
        com.example.backend.beneficio.entity.Beneficio updated = service.update(5L, b);
        assertEquals("B", updated.getNome());
        verify(remote).atualizar(eq(5L), eq("B"), eq("d2"), eq(new BigDecimal("33.00")), eq(Boolean.FALSE));
    }

    @Test
    @DisplayName("deleted deve delegar ao remote")
    void deleted() {
        service.deleted(9L);
        verify(remote).remover(9L);
    }

    @Test
    @DisplayName("transfer deve aplicar validações e atualizar ambos registros via remote")
    void transfer() {
        when(remote.buscarPorId(1L)).thenReturn(Optional.of(ejb(1L, "From", new BigDecimal("100.00"), true)));
        when(remote.buscarPorId(2L)).thenReturn(Optional.of(ejb(2L, "To", new BigDecimal("10.00"), true)));

        service.transfer(1L, 2L, new BigDecimal("30.00"));

        verify(remote, times(2)).atualizar(anyLong(), anyString(), anyString(), any(BigDecimal.class), anyBoolean());
    }

    @Test
    @DisplayName("transfer deve falhar com valor inválido, inativos ou saldo insuficiente")
    void transferInvalid() {
        when(remote.buscarPorId(1L)).thenReturn(Optional.of(ejb(1L, "From", new BigDecimal("10.00"), true)));
        when(remote.buscarPorId(2L)).thenReturn(Optional.of(ejb(2L, "To", new BigDecimal("10.00"), false)));

        assertThrows(IllegalArgumentException.class, () -> service.transfer(1L, 2L, new BigDecimal("0")));

        assertThrows(IllegalStateException.class, () -> service.transfer(1L, 2L, new BigDecimal("5.00")));

        when(remote.buscarPorId(2L)).thenReturn(Optional.of(ejb(2L, "To", new BigDecimal("10.00"), true)));
        assertThrows(IllegalStateException.class, () -> service.transfer(1L, 2L, new BigDecimal("50.00")));
    }

    @Test
    @DisplayName("transfer deve falhar quando origem/destino não existem")
    void transferNotFound() {
        when(remote.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.transfer(1L, 2L, new BigDecimal("1.00")));
        when(remote.buscarPorId(1L)).thenReturn(Optional.of(ejb(1L, "From", new BigDecimal("10.00"), true)));
        when(remote.buscarPorId(2L)).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.transfer(1L, 2L, new BigDecimal("1.00")));
    }
}
