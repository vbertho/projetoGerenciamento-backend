package com.example.projetoGerenciamento.repository;

import com.example.projetoGerenciamento.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
