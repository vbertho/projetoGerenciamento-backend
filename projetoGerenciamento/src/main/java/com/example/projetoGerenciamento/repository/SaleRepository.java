package com.example.projetoGerenciamento.repository;

import com.example.projetoGerenciamento.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Integer> {
}
