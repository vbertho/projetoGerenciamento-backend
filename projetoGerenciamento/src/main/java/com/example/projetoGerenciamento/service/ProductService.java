package com.example.projetoGerenciamento.service;
import com.example.projetoGerenciamento.dto.ProductRequestDTO;
import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.example.projetoGerenciamento.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    //dependency injection
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    //create
    public ProductResponseDTO  create (ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());

        Product saved = repository.save(product);

        return mapToResponse(saved);
    }

    //list all
    public List<ProductResponseDTO> listAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //update
    public ProductResponseDTO  update(Integer id, ProductRequestDTO dto) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Product updated = repository.save(product);

        return mapToResponse(updated);
    }

    //delete
    public void delete(Integer id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        repository.delete(product);

    }

    //update quantity
    public ProductResponseDTO updateQuantity(Integer id, int value) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        product.setQuantity(value);
        repository.save(product);

        return mapToResponse(product);
    }

    //convert product to ProductReponseDTO
    private ProductResponseDTO mapToResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
