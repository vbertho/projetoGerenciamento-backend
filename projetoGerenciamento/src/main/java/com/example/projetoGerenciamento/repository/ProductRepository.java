package com.example.projetoGerenciamento.repository;
import com.example.projetoGerenciamento.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByActiveTrue();
}
