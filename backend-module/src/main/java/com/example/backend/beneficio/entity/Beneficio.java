package com.example.backend.beneficio.entity;

import com.example.backend.beneficio.dto.CreateUpdateBeneficioDto;
import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "BENEFICIO")
@Schema(name = "Beneficio", description = "Entidade de Benefício")
public class Beneficio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nome do benefício", example = "Vale Alimentação")
    private String nome;

    @Column(length = 255)
    @Schema(description = "Descrição do benefício", example = "Benefício mensal para alimentação")
    private String descricao;

    @Column(nullable = false, precision = 15, scale = 2)
    @Schema(description = "Valor do benefício", example = "350.00")
    private BigDecimal valor;

    @Column
    @Schema(description = "Flag de ativo", example = "true")
    private Boolean ativo = Boolean.TRUE;

}
