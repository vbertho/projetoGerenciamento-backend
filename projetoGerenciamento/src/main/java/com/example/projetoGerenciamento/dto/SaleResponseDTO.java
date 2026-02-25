package com.example.projetoGerenciamento.dto;

import java.time.LocalDate;
import java.util.List;

public class SaleResponseDTO {
    private final Integer saleId;
    private final LocalDate saleDate;
    private final List<SoldProductResponse> products;

    public SaleResponseDTO(Integer saleId, LocalDate saleDate, List<SoldProductResponse> products) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.products = products;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public List<SoldProductResponse> getProducts() {
        return products;
    }

    public static class SoldProductResponse {
        private final Integer productId;
        private final String productName;
        private final Integer quantity;
        private final Double unitPrice;

        public SoldProductResponse(Integer productId, String productName, Integer quantity, Double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Integer getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Double getUnitPrice() {
            return unitPrice;
        }
    }
}
