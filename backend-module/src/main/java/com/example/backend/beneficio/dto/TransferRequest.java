package com.example.backend.beneficio.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Schema(name = "TransferRequest", description = "Requisição para transferência de valores entre benefícios")
public record TransferRequest(
        @NonNull
        @Schema(description = "ID de origem", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long fromId,
        @NonNull
        @Schema(description = "ID de destino", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        Long toId,

        @Positive(message = "Valor a transferir deve ser positivo")
        @Schema(description = "Valor a transferir", example = "50.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal amount

) {

}
