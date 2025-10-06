package com.example.ejb;

import com.example.ejb.entity.Beneficio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;

/**
 *  Caso nao consiga se conectar utilizar essa conexao fake para simular
 *
 */
public class DevRunner {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bipPU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Beneficio b = new Beneficio();
            b.setNome("Vale Refeição");
            b.setDescricao("Benefício de alimentação");
            b.setValor(new BigDecimal("500.00"));
            b.setAtivo(Boolean.TRUE);
            em.persist(b);
            tx.commit();

            System.out.println("Benefício salvo com ID: " + b.getId());
            System.out.println("Total de benefícios: " + em.createQuery("select count(b) from Beneficio b", Long.class).getSingleResult());
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
            emf.close();
        }
    }
}
