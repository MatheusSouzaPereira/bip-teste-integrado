package com.example.backend.beneficio.service;

import com.example.backend.beneficio.entity.Beneficio;
import com.example.backend.beneficio.repository.BeneficioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class BeneficioTestServiceImplTest {

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private BeneficioRepository repository;

    private Beneficio novo(String nome, BigDecimal valor, boolean ativo) {
        Beneficio b = new Beneficio();
        b.setNome(nome);
        b.setDescricao("desc " + nome);
        b.setValor(valor);
        b.setAtivo(ativo);
        return b;
    }

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("create e findById devem persistir e recuperar um benefício")
    void createAndFind() {
        Beneficio saved = beneficioService.create(novo("A", new BigDecimal("100.00"), true));
        assertNotNull(saved.getId());
        Beneficio found = beneficioService.findById(saved.getId());
        assertEquals("A", found.getNome());
        assertEquals(new BigDecimal("100.00"), found.getValor());
        assertTrue(Boolean.TRUE.equals(found.getAtivo()));
    }

    @Test
    @DisplayName("update deve atualizar todos os campos editáveis")
    void update() {
        Beneficio saved = beneficioService.create(novo("A", new BigDecimal("100.00"), true));
        Beneficio changes = novo("B", new BigDecimal("250.00"), false);
        Beneficio updated = beneficioService.update(saved.getId(), changes);
        assertEquals("B", updated.getNome());
        assertEquals(new BigDecimal("250.00"), updated.getValor());
        assertEquals(false, updated.getAtivo());
    }

    @Test
    @DisplayName("deleted deve remover o registro")
    void deleteById() {
        Beneficio saved = beneficioService.create(novo("A", new BigDecimal("100.00"), true));
        beneficioService.deleted(saved.getId());
        assertThrows(EntityNotFoundException.class, () -> beneficioService.findById(saved.getId()));
    }

    @Test
    @DisplayName("list deve retornar todos os registros")
    void listAll() {
        beneficioService.create(novo("A", new BigDecimal("100.00"), true));
        beneficioService.create(novo("B", new BigDecimal("200.00"), true));
        List<Beneficio> list = beneficioService.list();
        assertEquals(2, list.size());
    }

    @Nested
    class Transfer {
        @Test
        @DisplayName("transfer sucesso movimenta os saldos")
        void transferSuccess() {
            Beneficio from = beneficioService.create(novo("From", new BigDecimal("300.00"), true));
            Beneficio to = beneficioService.create(novo("To", new BigDecimal("50.00"), true));

            beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("120.00"));

            Beneficio fromReload = beneficioService.findById(from.getId());
            Beneficio toReload = beneficioService.findById(to.getId());
            assertEquals(new BigDecimal("180.00"), fromReload.getValor());
            assertEquals(new BigDecimal("170.00"), toReload.getValor());
        }

        @Test
        @DisplayName("transfer deve falhar se origem e destino são iguais")
        void transferSameAccount() {
            Beneficio from = beneficioService.create(novo("From", new BigDecimal("300.00"), true));
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> beneficioService.transfer(from.getId(), from.getId(), new BigDecimal("10.00")));
            assertTrue(ex.getMessage().toLowerCase().contains("origem e destino"));
        }

        @Test
        @DisplayName("transfer deve falhar com valor nulo, zero ou negativo")
        void transferInvalidAmount() {
            Beneficio from = beneficioService.create(novo("From", new BigDecimal("300.00"), true));
            Beneficio to = beneficioService.create(novo("To", new BigDecimal("50.00"), true));

            assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(from.getId(), to.getId(), null));
            assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("0")));
            assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("-1")));
        }

        @Test
        @DisplayName("transfer deve falhar quando origem ou destino estão inativos")
        void transferInactive() {
            Beneficio from = beneficioService.create(novo("From", new BigDecimal("300.00"), false));
            Beneficio to = beneficioService.create(novo("To", new BigDecimal("50.00"), true));
            assertThrows(IllegalStateException.class, () -> beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("10.00")));
        }

        @Test
        @DisplayName("transfer deve falhar quando saldo é insuficiente")
        void transferInsufficient() {
            Beneficio from = beneficioService.create(novo("From", new BigDecimal("5.00"), true));
            Beneficio to = beneficioService.create(novo("To", new BigDecimal("50.00"), true));
            assertThrows(IllegalStateException.class, () -> beneficioService.transfer(from.getId(), to.getId(), new BigDecimal("10.00")));
        }

        @Test
        @DisplayName("transfer deve falhar quando origem ou destino não existem")
        void transferNotFound() {
            Beneficio to = beneficioService.create(novo("To", new BigDecimal("50.00"), true));
            assertThrows(EntityNotFoundException.class, () -> beneficioService.transfer(999L, to.getId(), new BigDecimal("10.00")));
            assertThrows(EntityNotFoundException.class, () -> beneficioService.transfer(to.getId(), 999L, new BigDecimal("10.00")));
        }
    }
}
