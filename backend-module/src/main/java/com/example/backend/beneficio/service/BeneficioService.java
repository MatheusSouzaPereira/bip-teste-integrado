package com.example.backend.beneficio.service;

import com.example.backend.beneficio.entity.Beneficio;
import com.example.backend.beneficio.repository.BeneficioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

public interface BeneficioService {

    List<Beneficio> list();
    Beneficio create(Beneficio b);
    void deleted(Long id);
    Beneficio findById(Long id);
    Beneficio update(Long id, Beneficio b);
    void transfer(Long fromId, Long toId, BigDecimal amount);
}
