package com.example.projetoGerenciamento.repository;
import com.example.projetoGerenciamento.model.Product;
import com.example.projetoGerenciamento.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {
    @Query("SELECT s FROM Stock s JOIN s.product p WHERE s.quantity > 0")
    List<Stock> findAvailableStock();
}
