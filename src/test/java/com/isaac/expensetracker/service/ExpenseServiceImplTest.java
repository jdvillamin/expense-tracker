package com.isaac.expensetracker.service;

import com.isaac.expensetracker.dto.ExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
import com.isaac.expensetracker.model.ExpenseCategory;

import com.isaac.expensetracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Expense Service Unit Tests")
public class ExpenseServiceImplTest {

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseServiceImpl();
    }

    @Test
    @DisplayName("Create expense should return created expense")
    void createExpense_shouldSaveExpenseWithGeneratedIdAndTimestamps() {
        // Given
        ExpenseRequest request = createValidRequest(
                "Balibago to BGC Bus",
                new BigDecimal("122.00"),
                ExpenseCategory.TRANSPORTATION,
                LocalDate.of(2026, 10, 15)
        );

        // When
        ExpenseResponse response = expenseService.createExpense(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getId());

        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getCategory(), response.getCategory());
        assertEquals(request.getExpenseDate(), response.getExpenseDate());

        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        assertEquals(response.getCreatedAt(), response.getUpdatedAt());
    }

    @Test
    @DisplayName("Create expense should generate unique IDs")
    void createExpense_shouldGenerateUniqueIds() {
        // Given
        ExpenseRequest request1 = createValidRequest(
                "Chicken Lunch",
                new BigDecimal("155.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 13)
        );

        ExpenseRequest request2 = createValidRequest(
                "BGC to Balibago Bus",
                new BigDecimal("122.00"),
                ExpenseCategory.TRANSPORTATION,
                LocalDate.of(2026, 10, 13)
        );

        // When
        ExpenseResponse response1 = expenseService.createExpense(request1);
        ExpenseResponse response2 = expenseService.createExpense(request2);
        ExpenseResponse response3 = expenseService.createExpense(request1);

        // Then
        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);

        assertNotNull(response1.getId());
        assertNotNull(response2.getId());
        assertNotNull(response3.getId());

        Set<Long> ids = new HashSet<>(List.of(
                response1.getId(),
                response2.getId(),
                response3.getId()
        ));

        assertEquals(3, ids.size());
    }

    @Test
    @DisplayName("Get all expenses should return all expenses")
    void getAllExpenses_shouldReturnAllExpenses() {
        // Given
        ExpenseRequest request1 = createValidRequest(
                "Chicken Lunch",
                new BigDecimal("155.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 13)
        );

        ExpenseRequest request2 = createValidRequest(
                "BGC to Balibago Bus",
                new BigDecimal("122.00"),
                ExpenseCategory.TRANSPORTATION,
                LocalDate.of(2026, 10, 13)
        );

        // When
        ExpenseResponse response1 = expenseService.createExpense(request1);
        ExpenseResponse response2 = expenseService.createExpense(request2);
        List<ExpenseResponse> getAllResponse = expenseService.getAllExpenses();

        // Then
        assertNotNull(getAllResponse);
        assertEquals(2, getAllResponse.size());

        ExpenseResponse actual1 = getAllResponse.stream()
                .filter(e -> e.getId().equals(response1.getId()))
                .findFirst()
                .orElseThrow();

        ExpenseResponse actual2 = getAllResponse.stream()
                .filter(e -> e.getId().equals(response2.getId()))
                .findFirst()
                .orElseThrow();

        matchResponses(response1, actual1);
        matchResponses(response2, actual2);
    }

    private ExpenseRequest createValidRequest(
            String description,
            BigDecimal amount,
            ExpenseCategory category,
            LocalDate expenseDate
    ) {
        return ExpenseRequest.builder()
                .description(description)
                .amount(amount)
                .category(category)
                .expenseDate(expenseDate)
                .build();
    }

    private void matchResponses(
            ExpenseResponse expectedResponse,
            ExpenseResponse testResponse
    ) {
        assertNotNull(expectedResponse);
        assertNotNull(testResponse);
        assertNotNull(testResponse.getId());
        assertNotNull(testResponse.getCreatedAt());
        assertNotNull(testResponse.getUpdatedAt());
        assertEquals(testResponse.getCreatedAt(), testResponse.getUpdatedAt());

        assertEquals(expectedResponse.getDescription(), testResponse.getDescription());
        assertEquals(expectedResponse.getAmount(), testResponse.getAmount());
        assertEquals(expectedResponse.getCategory(), testResponse.getCategory());
        assertEquals(expectedResponse.getExpenseDate(), testResponse.getExpenseDate());
    }
}
