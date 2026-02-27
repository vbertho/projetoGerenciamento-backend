package com.example.projetoGerenciamento.controller;

import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.model.Stock;
import com.example.projetoGerenciamento.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/totalQuantity")
    public Integer totalQuantitySold(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return service.totalQuantitySold(start, end);
    }

    @GetMapping("/unitsByProduct")
    public List<Object[]> totalUnitsSoldByProduct(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return service.totalUnitsSoldByProduct(start, end);
    }

    @GetMapping("/bestSelling")
    public String bestSellingProduct(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return service.bestSellingProduct(start, end);
    }

    @GetMapping("/totalAmount")
    public Double totalAmountByPeriod(@RequestParam LocalDate start, @RequestParam LocalDate end) {
        return service.totalAmountByPeriod(start, end);
    }

    @GetMapping("/availableStock")
    public List<ProductResponseDTO> findAvailableStock() {
        return service.findAvailableStock();
    }
}
