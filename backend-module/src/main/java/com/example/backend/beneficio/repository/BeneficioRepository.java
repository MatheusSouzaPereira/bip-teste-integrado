package com.example.backend.beneficio.repository;

import com.example.backend.beneficio.entity.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
}
