package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.dto.ProductRequestDTO;
import com.example.projetoGerenciamento.dto.SaleRequestDTO;
import com.example.projetoGerenciamento.dto.SaleResponseDTO;
import com.example.projetoGerenciamento.exception.ProductNotFoundException;
import com.example.projetoGerenciamento.model.*;
import com.example.projetoGerenciamento.repository.ProductRepository;
import com.example.projetoGerenciamento.repository.SaleRepository;
import com.example.projetoGerenciamento.repository.SoldProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
import com.example.projetoGerenciamento.security.AuthHelper;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    SaleRepository saleRepo;

    @Mock
    SoldProductRepository soldProductRepo;

    @Mock
    ProductRepository productRepo;

    @Mock
    StockRepository stockRepo;

    @Mock
    AuthHelper authHelper;

    @InjectMocks
    private SaleService saleService;

    User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setEmail("teste@gmail.com");
        currentUser.setPassword("123");
    }

    @Nested
    class createSale {

        @Test
        @DisplayName("Should create sale and decrease stock quantity")
        void shouldCreateSaleAndDecreaseStockQuantity() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);

            SaleRequestDTO.SoldProductRequest item = new SaleRequestDTO.SoldProductRequest();
            item.setProductId(1);
            item.setQuantity(2);

            SaleRequestDTO dto = new SaleRequestDTO();
            dto.setSaleDate(LocalDate.of(2026, 1, 15));
            dto.setProducts(List.of(item));

            Stock stock = new Stock();
            stock.setQuantity(10);

            Product product = new Product();
            product.setId(1);
            product.setName("Cellphone");
            product.setPrice(2500.00);
            product.setStock(stock);

            when(productRepo.findByIdAndUser(1, currentUser)).thenReturn(Optional.of(product));

            Sale sale = new Sale();
            sale.setId(1);
            sale.setSaleDate(dto.getSaleDate());
            sale.setUser(currentUser);
            when(saleRepo.save(any(Sale.class))).thenReturn(sale);

            //Act
            SaleResponseDTO response = saleService.createSale(dto);

            //Assert
            assertNotNull(response);
            assertEquals(1, response.getProducts().size());
            assertEquals("Cellphone", response.getProducts().get(0).getProductName());
            assertEquals(2, response.getProducts().get(0).getQuantity());
            assertEquals(2500.00, response.getProducts().get(0).getUnitPrice());

            ArgumentCaptor<Stock> captor = ArgumentCaptor.forClass(Stock.class);
            verify(stockRepo).save(captor.capture());
            assertEquals(8, captor.getValue().getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when product not found on create sale")
        void shouldThrowExceptionWhenProductNotFoundOnCreateSale() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);
            when(productRepo.findByIdAndUser(99, currentUser)).thenReturn(Optional.empty());

            SaleRequestDTO.SoldProductRequest item = new SaleRequestDTO.SoldProductRequest();
            item.setProductId(99);
            item.setQuantity(2);

            SaleRequestDTO dto = new SaleRequestDTO();
            dto.setSaleDate(LocalDate.of(2026, 1, 15));
            dto.setProducts(List.of(item));

            //Act + Assert
            ProductNotFoundException ex = assertThrows(
                    ProductNotFoundException.class,
                    () -> saleService.createSale(dto)
            );

            assertEquals("Product with id 99 not found", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock")
        void shouldThrowExceptionWhenInsufficientStock() {

            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);

            SaleRequestDTO.SoldProductRequest item = new SaleRequestDTO.SoldProductRequest();
            item.setProductId(1);
            item.setQuantity(2);

            SaleRequestDTO dto = new SaleRequestDTO();
            dto.setSaleDate(LocalDate.of(2026, 1, 15));
            dto.setProducts(List.of(item));

            Stock stock = new Stock();
            stock.setQuantity(1);

            Product product = new Product();
            product.setId(1);
            product.setName("Cellphone");
            product.setPrice(2500.00);
            product.setStock(stock);

            when(productRepo.findByIdAndUser(1, currentUser)).thenReturn(Optional.of(product));

            //Act + Assert
            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> saleService.createSale(dto)
            );

            assertEquals("Insufficient stock for product: Cellphone", ex.getMessage());
        }
    }
}