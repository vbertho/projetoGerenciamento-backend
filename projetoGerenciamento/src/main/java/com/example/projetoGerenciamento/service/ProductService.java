package com.example.projetoGerenciamento.service;
import com.example.projetoGerenciamento.dto.ProductRequestDTO;
import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.exception.ProductNotFoundException;
import com.example.projetoGerenciamento.model.*;
import com.example.projetoGerenciamento.repository.StockRepository;
import com.example.projetoGerenciamento.security.AuthHelper;
import org.springframework.stereotype.Service;
import com.example.projetoGerenciamento.repository.ProductRepository;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final StockRepository stockRepo;
    private final AuthHelper authHelper;

    //dependency injection
    public ProductService(ProductRepository productRepo,
                          StockRepository stockRepo,
                          AuthHelper authHelper) {
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
        this.authHelper = authHelper;
    }

    //create product and initialize stock with quantity 0
    public ProductResponseDTO create(ProductRequestDTO dto) {
        User currentUser = authHelper.getCurrentUser();

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setUser(currentUser);

        Product savedProduct = productRepo.save(product);

        Stock stock = new Stock();
        stock.setQuantity(0);
        stock.setProduct(savedProduct);

        Stock savedStock = stockRepo.save(stock);
        savedProduct.setStock(savedStock);

        return mapToResponse(savedProduct);
    }

    //list all active true
    public List<ProductResponseDTO> listAll() {
        User currentUser = authHelper.getCurrentUser();
        return productRepo.findByUserAndActiveTrue(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //update
    public ProductResponseDTO update(Integer id, ProductRequestDTO dto) {
        User currentUser = authHelper.getCurrentUser();
        Product product = productRepo.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Product updated = productRepo.save(product);

        return mapToResponse(updated);
    }

    //delete - soft delete
    public void delete(Integer id) {
        User currentUser = authHelper.getCurrentUser();
        Product product = productRepo.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setActive(false);
        productRepo.save(product);
    }

    //update quantity
    public ProductResponseDTO updateQuantity(Integer id, int value) {
        User currentUser = authHelper.getCurrentUser();
        Product product = productRepo.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Stock stock = product.getStock();
        stock.setQuantity(value);
        stockRepo.save(stock);

        return mapToResponse(product);
    }

    //convert product to ProductReponseDTO
    private ProductResponseDTO mapToResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock().getQuantity()
        );
    }
}
