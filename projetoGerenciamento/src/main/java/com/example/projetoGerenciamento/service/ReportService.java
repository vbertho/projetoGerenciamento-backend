package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.model.User;
import com.example.projetoGerenciamento.repository.SoldProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
import com.example.projetoGerenciamento.security.AuthHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final SoldProductRepository soldProductRepo;
    private final StockRepository stockRepo;
    private final AuthHelper authHelper;

    public ReportService(SoldProductRepository soldProductRepo,
                         StockRepository stockRepo,
                         AuthHelper authHelper) {
        this.soldProductRepo = soldProductRepo;
        this.stockRepo = stockRepo;
        this.authHelper = authHelper;
    }

    // total quantity sold in a period
    public Integer totalQuantitySold(LocalDate start, LocalDate end) {
        User currentUser = authHelper.getCurrentUser();
        return soldProductRepo.totalQuantitySold(start, end, currentUser.getId());
    }

    // total units sold by product in a period
    public List<Object[]> totalUnitsSoldByProduct(LocalDate start, LocalDate end) {
        User currentUser = authHelper.getCurrentUser();
        return soldProductRepo.totalUnitsSoldByProduct(start, end, currentUser.getId());
    }

    // best-selling product in a period
    public String bestSellingProduct(LocalDate start, LocalDate end) {
        User currentUser = authHelper.getCurrentUser();
        return soldProductRepo.bestSellingProduct(start, end, currentUser.getId());
    }

    // total amount earned in a period
    public Double totalAmountByPeriod(LocalDate start, LocalDate end) {
        User currentUser = authHelper.getCurrentUser();
        return soldProductRepo.totalAmountByPeriod(start, end, currentUser.getId());
    }

    // products with available stock belonging to the logged user
    public List<ProductResponseDTO> findAvailableStock() {
        User currentUser = authHelper.getCurrentUser();
        return stockRepo.findAvailableStock(currentUser.getId()).stream()
                .map(s -> new ProductResponseDTO(s.getProduct().getId(), s.getProduct().getName(), s.getProduct().getPrice(), s.getQuantity()))
                .toList();
    }
}