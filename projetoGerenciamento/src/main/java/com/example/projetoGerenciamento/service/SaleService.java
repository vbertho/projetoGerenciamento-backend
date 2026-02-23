package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.dto.SaleRequestDTO;
import com.example.projetoGerenciamento.dto.SaleResponseDTO;
import com.example.projetoGerenciamento.model.Product;
import com.example.projetoGerenciamento.model.Sale;
import com.example.projetoGerenciamento.model.SoldProduct;
import com.example.projetoGerenciamento.model.Stock;
import com.example.projetoGerenciamento.repository.ProductRepository;
import com.example.projetoGerenciamento.repository.SaleRepository;
import com.example.projetoGerenciamento.repository.SoldProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
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

    public SaleService(SaleRepository saleRepo,
                       SoldProductRepository soldProductRepo,
                       ProductRepository productRepo,
                       StockRepository stockRepo) {
        this.saleRepo = saleRepo;
        this.soldProductRepo = soldProductRepo;
        this.productRepo = productRepo;
        this.stockRepo = stockRepo;
    }

    //create sale and sold products, decrease stock quantity
    //rolls back all db changes if any error occurs
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO dto) {
        Sale sale = new Sale();
        sale.setSaleDate(dto.getSaleDate());
        sale.setTotalAmount(0.0);
        saleRepo.save(sale);

        //accumulator variable for total sale amount
        double total = 0.0;
        List<SoldProduct> soldProducts = new ArrayList<>();

        //for each product (item) in the request list
        for (SaleRequestDTO.SoldProductRequest item : dto.getProducts()) {
            Product product = productRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            //check if quantity is available
            Stock stock = product.getStock();
            if (stock.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
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
            //calculate the total amount by multiplying the price by the quantity
            total += product.getPrice() * item.getQuantity();
        }

        //save all sold products and update the sale total
        soldProductRepo.saveAll(soldProducts);

        sale.setTotalAmount(total);
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
                sale.getTotalAmount(),
                products
        );
    }
}
