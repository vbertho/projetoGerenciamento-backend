package com.example.projetoGerenciamento.controller;
import com.example.projetoGerenciamento.dto.SaleRequestDTO;
import com.example.projetoGerenciamento.dto.SaleResponseDTO;
import com.example.projetoGerenciamento.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/sales")
public class SaleController {
    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @PostMapping
    public SaleResponseDTO createSale(@RequestBody @Valid SaleRequestDTO dto) {
        return service.createSale(dto);
    }
}
