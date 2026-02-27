package com.example.projetoGerenciamento.repository;

import com.example.projetoGerenciamento.model.SoldProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SoldProductRepository extends JpaRepository<SoldProduct, Integer> {
    @Query("SELECT SUM(sp.quantity) FROM SoldProduct sp JOIN sp.sale s WHERE s.saleDate BETWEEN :start AND :end")
    Integer totalQuantitySold(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT p.name, SUM(sp.quantity) FROM SoldProduct sp JOIN sp.product p JOIN sp.sale s WHERE s.saleDate BETWEEN :start AND :end GROUP BY p.id, p.name")
    List<Object[]> totalUnitsSoldByProduct(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT p.name FROM SoldProduct sp JOIN sp.product p JOIN sp.sale s WHERE s.saleDate BETWEEN :start AND :end GROUP BY p.id, p.name ORDER BY SUM(sp.quantity) DESC LIMIT 1")
    String bestSellingProduct(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT SUM(sp.unitPrice * sp.quantity) FROM SoldProduct sp JOIN sp.sale s WHERE s.saleDate BETWEEN :start AND :end")
    Double totalAmountByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
