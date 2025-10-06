package com.example.backend.beneficio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(name = "CreateUpdateBeneficio", description = "Dados para criação/atualização de Benefício")
public record CreateUpdateBeneficioDto(
        @Schema(description = "Nome do benefício", example = "Vale Alimentação", requiredMode = Schema.RequiredMode.REQUIRED)
         String nome,
        @Schema(description = "Descrição do benefício", example = "Benefício mensal para alimentação")
        String descricao,
        @Schema(description = "Valor monetário do benefício", example = "350.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal valor,
        @Schema(description = "Indica se o benefício está ativo", example = "true")
        Boolean ativo
) {
}
