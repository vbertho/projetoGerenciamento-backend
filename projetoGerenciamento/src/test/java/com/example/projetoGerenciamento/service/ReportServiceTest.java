package com.example.projetoGerenciamento.service;

import com.example.projetoGerenciamento.dto.ProductResponseDTO;
import com.example.projetoGerenciamento.model.Product;
import com.example.projetoGerenciamento.model.Stock;
import com.example.projetoGerenciamento.model.User;
import com.example.projetoGerenciamento.repository.SoldProductRepository;
import com.example.projetoGerenciamento.repository.StockRepository;
import com.example.projetoGerenciamento.security.AuthHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @Mock
    SoldProductRepository soldProductRepo;

    @Mock
    StockRepository stockRepo;

    @Mock
    AuthHelper authHelper;

    @InjectMocks
    private ReportService reportService;

    User currentUser;

    LocalDate start = LocalDate.of(2026, 1, 1);
    LocalDate end = LocalDate.of(2026, 12, 31);

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1);
        currentUser.setEmail("teste@gmail.com");
        currentUser.setPassword("123");
    }

    @Test
    @DisplayName("Should return total quantity sold")
    void shouldReturnTotalQuantitySold() {
        //Arrange
        when(authHelper.getCurrentUser()).thenReturn(currentUser);
        when(soldProductRepo.totalQuantitySold(start, end, 1)).thenReturn(10);

        //Act
        Integer response = reportService.totalQuantitySold(start, end);

        //Assert
        assertEquals(10, response);
    }

    @Test
    @DisplayName("Should return total units sold by product")
    void shouldReturnTotalUnitsSoldByProduct() {
        //Arrange
        when(authHelper.getCurrentUser()).thenReturn(currentUser);

        List<Object[]> expected = List.of(
                new Object[]{"Cellphone", 5},
                new Object[]{"Notebook", 3}
        );

        when(soldProductRepo.totalUnitsSoldByProduct(start, end, 1)).thenReturn(expected);

        //Act
        List<Object[]> response = reportService.totalUnitsSoldByProduct(start, end);

        //Assert
        assertEquals(expected, response);

    }

    @Test
    @DisplayName("Should return best selling product")
    void shouldReturnBestSellingProduct() {
        //Arrange
        when(authHelper.getCurrentUser()).thenReturn(currentUser);
        when(soldProductRepo.bestSellingProduct(start, end, 1)).thenReturn("Cellphone");

        //Act
        String response = reportService.bestSellingProduct(start, end);

        //Assert
        assertEquals("Cellphone", response);

    }

    @Test
    @DisplayName("Should return total amount by period")
    void shouldReturnTotalAmountByPeriod() {
        //Arrange
        when(authHelper.getCurrentUser()).thenReturn(currentUser);
        when(soldProductRepo.totalAmountByPeriod(start, end, 1)).thenReturn(2500.00);

        //Act
        Double response = reportService.totalAmountByPeriod(start, end);

        //Assert
        assertEquals(2500.00, response);

    }

    @Test
    @DisplayName("Should return available stock")
    void shouldReturnAvailableStock() {
        //Arrange
        when(authHelper.getCurrentUser()).thenReturn(currentUser);

        Product product = new Product();
        product.setId(1);
        product.setName("Cellphone");
        product.setPrice(2500.00);

        Stock stock = new Stock();
        stock.setQuantity(10);
        stock.setProduct(product);

        when(stockRepo.findAvailableStock(1)).thenReturn(List.of(stock));

        //Act
        List<ProductResponseDTO> response = reportService.findAvailableStock();

        //Assert
        assertEquals(1, response.size());
        assertEquals("Cellphone", response.get(0).getName());
        assertEquals(10, response.get(0).getQuantity());
    }
}
