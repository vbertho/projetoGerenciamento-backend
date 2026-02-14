package com.example.projetoGerenciamento.dto;

public class ProductResponseDTO {

    private final Integer id;
    private final String name;
    private final Double price;
    private final Integer quantity;

    public ProductResponseDTO(Integer id, String name, Double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
