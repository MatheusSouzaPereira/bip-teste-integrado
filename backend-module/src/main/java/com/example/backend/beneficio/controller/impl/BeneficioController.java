package com.example.backend.beneficio.controller.impl;

import com.example.backend.beneficio.controller.BeneficioApi;
import com.example.backend.beneficio.dto.CreateUpdateBeneficioDto;
import com.example.backend.beneficio.dto.TransferRequest;
import com.example.backend.beneficio.entity.Beneficio;
import com.example.backend.beneficio.service.BeneficioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController implements BeneficioApi {

    private final BeneficioService service;

    public BeneficioController(BeneficioService service) {
        this.service = service;
    }

    @Override
    public List<Beneficio> list() {
        return service.list();
    }

    @Override
    public Beneficio findById(@Valid @PathVariable Long id) {
        return service.findById(id);
    }

    @Override
    public ResponseEntity<Beneficio> create(@RequestBody CreateUpdateBeneficioDto dto) {
        Beneficio b = new Beneficio();
        b.setNome(dto.nome());
        b.setDescricao(dto.descricao());
        b.setValor(dto.valor());
        b.setAtivo(dto.ativo());

        Beneficio saved = service.create(b);
        return ResponseEntity.ok().body(saved);
    }

    @Override
    public Beneficio update( @PathVariable Long id, @RequestBody CreateUpdateBeneficioDto dto) {
        Beneficio b = new Beneficio();
        b.setNome(dto.nome());
        b.setDescricao(dto.descricao());
        b.setValor(dto.valor());
        b.setAtivo(dto.ativo());

        return service.update(id, b);
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleted(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> transfer(@RequestBody TransferRequest req) {
        service.transfer(req.fromId(), req.toId(), req.amount());
        return ResponseEntity.ok().build();
    }
}
