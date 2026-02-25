package com.example.projetoGerenciamento.service;
import com.example.projetoGerenciamento.dto.ProductRequestDTO;
import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.model.Product;
import com.example.projetoGerenciamento.model.Stock;
import com.example.projetoGerenciamento.repository.StockRepository;
import org.springframework.stereotype.Service;
import com.example.projetoGerenciamento.repository.ProductRepository;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final StockRepository stockRepo;

    //dependency injection
    public ProductService(ProductRepository productRepo,
                          StockRepository stockRepo) {
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
    }

    //create product and initialize stock with quantity 0
    public ProductResponseDTO  create(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Product savedProduct = productRepo.save(product);

        Stock stock = new Stock();
        stock.setQuantity(0);
        stock.setProduct(savedProduct);

        Stock savedStock = stockRepo.save(stock);
        savedProduct.setStock(savedStock);

        return mapToResponse(savedProduct);
    }

    //list all - soft delete
    public List<ProductResponseDTO> listAll() {
        return productRepo.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //update
    public ProductResponseDTO  update(Integer id, ProductRequestDTO dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Product updated = productRepo.save(product);

        return mapToResponse(updated);
    }

    //delete - soft delete
    public void delete(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepo.save(product);
    }

    //update quantity
    public ProductResponseDTO updateQuantity(Integer id, int value) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

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
