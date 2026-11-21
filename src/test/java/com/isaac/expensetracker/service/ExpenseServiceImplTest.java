package com.isaac.expensetracker.service;

import com.isaac.expensetracker.dto.CreateExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
import com.isaac.expensetracker.dto.UpdateExpenseRequest;
import com.isaac.expensetracker.exception.ExpenseNotFoundException;
import com.isaac.expensetracker.model.ExpenseCategory;

import com.isaac.expensetracker.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
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
        CreateExpenseRequest request = createValidCreateExpenseRequest(
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
        assertExpenseMatchesRequest(request, response);
        assertTimestampsInitialized(response);
    }

    @Test
    @DisplayName("Create expense should generate unique IDs")
    void createExpense_shouldGenerateUniqueIds() {
        // Given
        CreateExpenseRequest request1 = createValidCreateExpenseRequest(
                "Chicken Lunch",
                new BigDecimal("155.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 13)
        );

        CreateExpenseRequest request2 = createValidCreateExpenseRequest(
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
        CreateExpenseRequest request1 = createValidCreateExpenseRequest(
                "Chicken Lunch",
                new BigDecimal("155.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 13)
        );

        CreateExpenseRequest request2 = createValidCreateExpenseRequest(
                "BGC to Balibago Bus",
                new BigDecimal("122.00"),
                ExpenseCategory.TRANSPORTATION,
                LocalDate.of(2026, 10, 13)
        );

        // When
        ExpenseResponse response1 = expenseService.createExpense(request1);
        ExpenseResponse response2 = expenseService.createExpense(request2);
        List<ExpenseResponse> responses = expenseService.getAllExpenses();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        ExpenseResponse actual1 = responses.stream()
                .filter(e -> e.getId().equals(response1.getId()))
                .findFirst()
                .orElseThrow();

        ExpenseResponse actual2 = responses.stream()
                .filter(e -> e.getId().equals(response2.getId()))
                .findFirst()
                .orElseThrow();

        assertExpenseEquals(response1, actual1);
        assertExpenseEquals(response2, actual2);
    }

    @Test
    @DisplayName("Get expense by ID should return expense")
    void getExpenseById_shouldReturnExpense() {
        // Given
        CreateExpenseRequest request = createValidCreateExpenseRequest(
                "Chicken Lunch",
                new BigDecimal("155.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 13)
        );

        // When
        ExpenseResponse expected = expenseService.createExpense(request);
        ExpenseResponse actual = expenseService.getExpenseById(expected.getId());

        // Then
        assertExpenseEquals(expected, actual);
    }

    @Test
    @DisplayName("Get expense by ID when not found should throw not found exception")
    void getExpenseById_whenNotFound_shouldThrowNotFoundException() {
        // Given
        Long nonExistingId = 999L;

        // When/Then
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpenseById(nonExistingId));
    }

    @Test
    @DisplayName("Update expense should update expense and timestamp")
    void updateExpense_shouldUpdateExpenseAndTimestamp() {
        // Given
        CreateExpenseRequest createRequest = createValidCreateExpenseRequest(
                "Chicken Lunch",
                new BigDecimal("155.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 13)
        );

        UpdateExpenseRequest updateRequest = createValidUpdateExpenseRequest(
                "Beef Lunch",
                new BigDecimal("200.00"),
                ExpenseCategory.LEISURE,
                LocalDate.of(2026, 10, 15)
        );

        // When
        ExpenseResponse createResponse = expenseService.createExpense(createRequest);
        ExpenseResponse response = expenseService.updateExpense(createResponse.getId(), updateRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(createResponse.getId(), response.getId());
        assertEquals(createResponse.getCreatedAt(), response.getCreatedAt());
        assertExpenseMatchesRequest(updateRequest, response);
        assertTimestampsUpdated(createResponse.getUpdatedAt(), response);

        ExpenseResponse getResponse = expenseService.getExpenseById(response.getId());
        assertExpenseEquals(response, getResponse);
    }

    @Test
    @DisplayName("Update expense when not found should throw not found exception")
    void updateExpense_whenNotFound_shouldThrowNotFoundException() {
        // Given
        Long nonExistingId = 999L;
        UpdateExpenseRequest updateRequest = createValidUpdateExpenseRequest(
                "Pork Lunch",
                new BigDecimal("300.00"),
                ExpenseCategory.FOOD,
                LocalDate.of(2026, 10, 14)
        );

        // When/Then
        assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateExpense(nonExistingId, updateRequest));
    }

    private CreateExpenseRequest createValidCreateExpenseRequest(
            String description,
            BigDecimal amount,
            ExpenseCategory category,
            LocalDate expenseDate
    ) {
        return CreateExpenseRequest.builder()
                .description(description)
                .amount(amount)
                .category(category)
                .expenseDate(expenseDate)
                .build();
    }

    private UpdateExpenseRequest createValidUpdateExpenseRequest(
            String description,
            BigDecimal amount,
            ExpenseCategory category,
            LocalDate expenseDate
    ) {
        return UpdateExpenseRequest.builder()
                .description(description)
                .amount(amount)
                .category(category)
                .expenseDate(expenseDate)
                .build();
    }

    private void assertExpenseMatchesRequest(
            CreateExpenseRequest expected,
            ExpenseResponse actual
    ) {
        assertNotNull(actual);

        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getExpenseDate(), actual.getExpenseDate());
    }

    private void assertExpenseMatchesRequest(
            UpdateExpenseRequest expected,
            ExpenseResponse actual
    ) {
        assertNotNull(actual);

        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getExpenseDate(), actual.getExpenseDate());
    }

    private void assertExpenseEquals(
            ExpenseResponse expected,
            ExpenseResponse actual
    ) {
        assertNotNull(actual);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getCategory(), actual.getCategory());
        assertEquals(expected.getExpenseDate(), actual.getExpenseDate());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
    }

    private void assertTimestampsInitialized(ExpenseResponse response) {
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertEquals(response.getCreatedAt(), response.getUpdatedAt());
    }

    private void assertTimestampsUpdated(Instant before, ExpenseResponse response) {
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());

        assertTrue(response.getUpdatedAt().isAfter(response.getCreatedAt()));
        assertTrue(response.getUpdatedAt().isAfter(before));
    }
}
