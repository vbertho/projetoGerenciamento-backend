package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.dto.ProductRequestDTO;
import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.exception.ProductNotFoundException;
import com.example.projetoGerenciamento.model.Product;
import com.example.projetoGerenciamento.model.Stock;
import com.example.projetoGerenciamento.model.User;
import com.example.projetoGerenciamento.repository.ProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
import com.example.projetoGerenciamento.security.AuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    ProductRepository productRepo;

    @Mock
    StockRepository stockRepo;

    @Mock
    AuthHelper authHelper;

    @InjectMocks
    private ProductService productService;

    User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setEmail("teste@gmail.com");
        currentUser.setPassword("123");
    }

    @Nested
    class create{

        @Test
        @DisplayName("Should create product and initialize stock quantity with 0")
        void shouldCreateProductWithEmptyStock() {
            // Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);

            ProductRequestDTO dto = new ProductRequestDTO();
            dto.setName("Cellphone");
            dto.setPrice(2500.00);

            Product savedProduct = new Product();
            savedProduct.setId(1);
            savedProduct.setName("Cellphone");
            savedProduct.setPrice(2500.00);
            savedProduct.setUser(currentUser);
            when(productRepo.save(any(Product.class))).thenReturn(savedProduct);

            Stock savedStock = new Stock();
            savedStock.setQuantity(0);
            savedStock.setProduct(savedProduct);
            when(stockRepo.save(any(Stock.class))).thenReturn(savedStock);
            savedProduct.setStock(savedStock);

            //Act
            ProductResponseDTO response = productService.create(dto);

            // Assert
            assertNotNull(response);
            assertEquals("Cellphone", response.getName());
            assertEquals(2500.00, response.getPrice());
            assertEquals(0, response.getQuantity());

            verify(productRepo).save(any(Product.class));
            verify(stockRepo).save(any(Stock.class));
        }
    }

    @Test
    @DisplayName("Should return all active products from current user")
    void shouldReturnAllActiveProductsFromCurrentUser() {
        //Arrange
        when(authHelper.getCurrentUser()).thenReturn(currentUser);

        Stock stock = new Stock();
        stock.setQuantity(10);

        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setName("Cellphone");
        savedProduct.setPrice(2500.00);
        savedProduct.setStock(stock);

        when(productRepo.findByUserAndActiveTrue(currentUser)).thenReturn(List.of(savedProduct));

        //Act
        List<ProductResponseDTO> response = productService.listAll();

        //Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Cellphone", response.get(0).getName());
        assertEquals(2500.00, response.get(0).getPrice());
        assertEquals(10, response.get(0).getQuantity());

        verify(productRepo).findByUserAndActiveTrue(currentUser);
    }

    @Nested
    class update {

        @Test
        @DisplayName("Should update product name and price")
        void shouldUpdateProductNameAndPrice() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);

            ProductRequestDTO dto = new ProductRequestDTO();
            dto.setName("Notebook");
            dto.setPrice(4500.00);

            Product savedProduct = new Product();
            savedProduct.setId(1);
            savedProduct.setName("Cellphone");
            savedProduct.setPrice(2500.00);
            savedProduct.setUser(currentUser);

            Stock stock = new Stock();
            stock.setQuantity(0);
            savedProduct.setStock(stock);

            when(productRepo.findByIdAndUser(1, currentUser)).thenReturn(Optional.of(savedProduct));
            when(productRepo.save(any(Product.class))).thenReturn(savedProduct);

            //Act
            ProductResponseDTO response = productService.update(1, dto);

            //Assert
            assertNotNull(response);
            assertEquals("Notebook", response.getName());
            assertEquals(4500.00, response.getPrice());
            assertEquals(0, response.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when product not found on update")
        void shoudThrowExceptionWhenProductNotFoundOnUpdate() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);
            when(productRepo.findByIdAndUser(99, currentUser)).thenReturn(Optional.empty());

            ProductRequestDTO dto = new ProductRequestDTO();
            dto.setName("Notebook");
            dto.setPrice(4500.00);

            //Act + Assert
            ProductNotFoundException ex = assertThrows(
                    ProductNotFoundException.class,
                    () -> productService.update(99, dto)
            );

            assertEquals("Product with id 99 not found", ex.getMessage());
            verify(productRepo, never()).save(any());
        }
    }

    @Nested
    class delete {

        @Test
        @DisplayName("Should set product active false")
        void shouldSetProductActiveFalse() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);

            Product savedProduct = new Product();
            savedProduct.setId(1);
            savedProduct.setName("Cellphone");
            savedProduct.setPrice(2500.00);
            savedProduct.setUser(currentUser);

            when(productRepo.findByIdAndUser(1, currentUser)).thenReturn(Optional.of(savedProduct));

            //Act
            productService.delete(1);

            //Assert
            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            verify(productRepo).save(captor.capture());
            assertFalse(captor.getValue().isActive());
        }

        @Test
        @DisplayName("Should throw exception when product not found on delete")
        void shouldThrowExceptionWhenProductNotFoundOnDelete() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);
            when(productRepo.findByIdAndUser(99, currentUser)).thenReturn(Optional.empty());

            //Act + Assert
            ProductNotFoundException ex = assertThrows(
                    ProductNotFoundException.class,
                    () -> productService.delete(99)
            );

            assertEquals("Product with id 99 not found", ex.getMessage());
        }
    }

    @Nested
    class updateQuantity {

        @Test
        @DisplayName("Should update stock quantity")
        void shouldUpdateStockQuantity() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);

            Product savedProduct = new Product();
            savedProduct.setId(1);
            savedProduct.setName("Cellphone");
            savedProduct.setPrice(2500.00);
            savedProduct.setUser(currentUser);

            Stock savedStock = new Stock();
            savedStock.setQuantity(0);
            savedStock.setProduct(savedProduct);
            savedProduct.setStock(savedStock);

            when(productRepo.findByIdAndUser(1, currentUser)).thenReturn(Optional.of(savedProduct));

            //Act
            ProductResponseDTO response = productService.updateQuantity(1, 3);

            //Asset
            assertEquals(3, response.getQuantity());
        }

        @Test
        @DisplayName("Should throw exception when product not found on update quantity")
        void shouldThrowExceptionWhenProductNotFoundOnUpdateQuantity() {
            //Arrange
            when(authHelper.getCurrentUser()).thenReturn(currentUser);
            when(productRepo.findByIdAndUser(99, currentUser)).thenReturn(Optional.empty());

            //Act + Asset
            ProductNotFoundException ex = assertThrows(
                    ProductNotFoundException.class,
                    () -> productService.updateQuantity(99, 0)
            );

            assertEquals("Product with id 99 not found", ex.getMessage());
        }
    }
}