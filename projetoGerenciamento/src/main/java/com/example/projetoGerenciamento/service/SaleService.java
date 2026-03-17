package com.example.projetoGerenciamento.service;
import com.example.projetoGerenciamento.dto.SaleRequestDTO;
import com.example.projetoGerenciamento.dto.SaleResponseDTO;
import com.example.projetoGerenciamento.exception.InsufficientStockException;
import com.example.projetoGerenciamento.exception.ProductNotFoundException;
import com.example.projetoGerenciamento.model.*;
import com.example.projetoGerenciamento.repository.ProductRepository;
import com.example.projetoGerenciamento.repository.SaleRepository;
import com.example.projetoGerenciamento.repository.SoldProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
import com.example.projetoGerenciamento.security.AuthHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepo;
    private final SoldProductRepository soldProductRepo;
    private final ProductRepository productRepo;
    private final StockRepository stockRepo;
    private final AuthHelper authHelper;

    public SaleService(SaleRepository saleRepo,
                       SoldProductRepository soldProductRepo,
                       ProductRepository productRepo,
                       StockRepository stockRepo,
                       AuthHelper authHelper) {
        this.saleRepo = saleRepo;
        this.soldProductRepo = soldProductRepo;
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
        this.authHelper = authHelper;
    }

    //create sale and sold products, decrease stock quantity
    //rolls back all db changes if any error occurs
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        User currentUser = authHelper.getCurrentUser();

        Sale sale = new Sale();
        sale.setSaleDate(dto.getSaleDate());
        sale.setUser(currentUser);
        saleRepo.save(sale);

        List<SoldProduct> soldProducts = new ArrayList<>();

        //for each product (item) in the request list
        for (SaleRequestDTO.SoldProductRequest item : dto.getProducts()) {
            Product product = productRepo.findByIdAndUser(item.getProductId(), currentUser)
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

            //check if quantity is available
            Stock stock = product.getStock();
            if (stock.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(product.getName());
            }

            //decrease stock by the quantity sold
            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            stockRepo.save(stock);

            SoldProduct soldProduct = new SoldProduct();
            soldProduct.setProduct(product);
            soldProduct.setSale(sale);
            soldProduct.setQuantity(item.getQuantity());
            soldProduct.setUnitPrice(product.getPrice());

            soldProducts.add(soldProduct);
        }

        //save all sold products and update the sale total
        soldProductRepo.saveAll(soldProducts);

        saleRepo.save(sale);

        return mapToResponse(sale, soldProducts);
    }

    private SaleResponseDTO mapToResponse(Sale sale, List<SoldProduct> soldProducts) {
        List<SaleResponseDTO.SoldProductResponse> products = new ArrayList<>();

        for (SoldProduct sp : soldProducts) {
            SaleResponseDTO.SoldProductResponse response = new SaleResponseDTO.SoldProductResponse(
                    sp.getProduct().getId(),
                    sp.getProduct().getName(),
                    sp.getQuantity(),
                    sp.getUnitPrice()
            );
            products.add(response);
        }

        return new SaleResponseDTO(
                sale.getId(),
                sale.getSaleDate(),
                products
        );
    }
}
