package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.model.Stock;
import com.example.projetoGerenciamento.repository.SoldProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final SoldProductRepository soldProductRepo;
    private final StockRepository stockRepo;

    public ReportService(SoldProductRepository soldProductRepo,
                         StockRepository stockRepo) {
        this.soldProductRepo = soldProductRepo;
        this.stockRepo = stockRepo;
    }

    // total quantity sold in a period
    public Integer totalQuantitySold(LocalDate start, LocalDate end) {
        return soldProductRepo.totalQuantitySold(start, end);
    }

    // total units sold by product in a period
    public List<Object[]> totalUnitsSoldByProduct(LocalDate start, LocalDate end) {
        return soldProductRepo.totalUnitsSoldByProduct(start, end);
    }

    // best selling product in a period
    public Object[] bestSellingProduct(LocalDate start, LocalDate end) {
        return soldProductRepo.bestSellingProduct(start, end);
    }

    // total amount earned in a period
    public Double totalAmountByPeriod(LocalDate start, LocalDate end) {
        return soldProductRepo.totalAmountByPeriod(start, end);
    }

    // products with available stock
    public List<ProductResponseDTO> findAvailableStock() {
        return stockRepo.findAvailableStock().stream()
                .map(s -> new ProductResponseDTO(s.getProduct().getId(), s.getProduct().getName(), s.getProduct().getPrice(), s.getQuantity()))
                .toList();
    }
}
