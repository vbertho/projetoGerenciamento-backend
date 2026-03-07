package com.example.projetoGerenciamento.repository;
import com.example.projetoGerenciamento.model.Product;
import com.example.projetoGerenciamento.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // used before multi-tenant — kept for compatibility
    List<Product> findByActiveTrue();

    // returns all active products belonging to the logged user
    List<Product> findByUserAndActiveTrue(User user);

    // returns a product by id only if it belongs to the logged user
    Optional<Product> findByIdAndUser(Integer id, User user);
}