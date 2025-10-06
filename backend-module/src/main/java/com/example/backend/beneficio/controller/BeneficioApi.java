package com.example.backend.beneficio.controller;

import com.example.backend.beneficio.dto.CreateUpdateBeneficioDto;
import com.example.backend.beneficio.dto.TransferRequest;
import com.example.backend.beneficio.entity.Beneficio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Benefícios", description = "Operações de CRUD e transferências de benefícios")
public interface BeneficioApi {

    @GetMapping
    @Operation(summary = "Lista todos os benefícios")
    @ApiResponse(responseCode = "200", description = "Lista de benefícios retornada")
    List<Beneficio> list();

    @GetMapping("/{id}")
    @Operation(summary = "Busca benefício por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Benefício encontrado"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado", content = @Content)
    })
    Beneficio findById(@Parameter(description = "ID do benefício", example = "1") @PathVariable Long id);

    @PostMapping
    @Operation(summary = "Cria um novo benefício")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Benefício criado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Beneficio.class)))
    })
    ResponseEntity<Beneficio> create(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para criação") @RequestBody CreateUpdateBeneficioDto dto);


    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um benefício existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Benefício atualizado"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado", content = @Content)
    })
    Beneficio update(@Parameter(description = "ID do benefício", example = "1") @PathVariable Long id,
                     @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para atualização") @RequestBody CreateUpdateBeneficioDto dto);



    @DeleteMapping("/{id}")
    @Operation(summary = "Remove benefício por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Benefício não encontrado", content = @Content)
    })
    ResponseEntity<Void> delete(@Parameter(description = "ID do benefício", example = "1") @PathVariable Long id);

    @PostMapping("/transfer")
    @Operation(summary = "Transfere valor entre benefícios")
    @ApiResponse(responseCode = "200", description = "Transferência realizada")
    ResponseEntity<Void> transfer(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da transferência") @RequestBody TransferRequest req);

    }
