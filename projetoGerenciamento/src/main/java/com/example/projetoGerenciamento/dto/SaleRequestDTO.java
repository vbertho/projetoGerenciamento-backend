package com.example.projetoGerenciamento.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public class SaleRequestDTO {
    @NotNull(message = "Sale date is required")
    private LocalDate saleDate;

    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    private List<SoldProductRequest> products;

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public List<SoldProductRequest> getProducts() {
        return products;
    }

    public void setProducts(List<SoldProductRequest> products) {
        this.products = products;
    }

    public static class SoldProductRequest {
        @NotNull(message = "Product id is required")
        private Integer productId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
