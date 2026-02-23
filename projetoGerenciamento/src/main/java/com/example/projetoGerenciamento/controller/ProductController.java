package com.example.projetoGerenciamento.controller;
import com.example.projetoGerenciamento.dto.ProductRequestDTO;
import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.dto.StockRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.projetoGerenciamento.service.ProductService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    //dependency injection
    public ProductController(ProductService service) {
        this.service = service;
    }

    //create
    @PostMapping
    public ProductResponseDTO create (@RequestBody @Valid ProductRequestDTO dto) {
        return service.create(dto);
    }

    //list all
    @GetMapping
    public List<ProductResponseDTO> list() {
        return service.listAll();
    }

    //update
    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Integer id, @RequestBody @Valid ProductRequestDTO dto) {
        return service.update(id, dto);
    }

    //delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    //update quantity
    @PatchMapping("/{id}/updateQuantity")
    public ProductResponseDTO updateQuantity(
            @PathVariable Integer id,
            @RequestBody @Valid StockRequestDTO dto) {
        return service.updateQuantity(id, dto.getQuantity());
    }
}