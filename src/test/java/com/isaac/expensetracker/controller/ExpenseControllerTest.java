package com.isaac.expensetracker.controller;

import com.isaac.expensetracker.dto.ExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
import com.isaac.expensetracker.exception.ExpenseNotFoundException;
import com.isaac.expensetracker.model.ExpenseCategory;
import com.isaac.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
@DisplayName("Expense Controller Tests")
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    @DisplayName("Create expense controller test")
    void createExpense_whenValidRequest_shouldReturnCreated() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "Balibago to BGC Bus",
                    "amount": 122.00,
                    "category": "TRANSPORTATION",
                    "expenseDate": "2026-10-18"
                }
                """;

        ExpenseResponse expectedResponse = ExpenseResponse.builder()
                .id(1L)
                .description("Balibago to BGC Bus")
                .amount(new BigDecimal("122.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T10:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .build();

        when(expenseService.createExpense(any(ExpenseRequest.class))).thenReturn(expectedResponse);

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(expectedResponse.getId().intValue()))
                .andExpect(jsonPath("$.description")
                        .value(expectedResponse.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(expectedResponse.getAmount().doubleValue()))
                .andExpect(jsonPath("$.category")
                        .value(expectedResponse.getCategory().name()))
                .andExpect(jsonPath("$.expenseDate")
                        .value(expectedResponse.getExpenseDate().toString()))
                .andExpect(jsonPath("$.createdAt")
                        .value(expectedResponse.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt")
                        .value(expectedResponse.getUpdatedAt().toString()));

        verify(expenseService).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Get all expenses should return ok")
    void getAllExpenses_shouldReturnOk() throws Exception {
        // Given
        ExpenseResponse expense1 = ExpenseResponse.builder()
                .id(1L)
                .description("Balibago to BGC Bus")
                .amount(new BigDecimal("122.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T10:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .build();

        ExpenseResponse expense2 = ExpenseResponse.builder()
                .id(2L)
                .description("BGC to Balibago Bus")
                .amount(new BigDecimal("100.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T18:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T18:00:00Z"))
                .build();

        List<ExpenseResponse> expectedExpenses = List.of(
                expense1,
                expense2
        );

        when(expenseService.getAllExpenses()).thenReturn(expectedExpenses);

        // When/Then
        mockMvc.perform(
                        get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id")
                        .value(expense1.getId().intValue()))
                .andExpect(jsonPath("$[0].description")
                        .value(expense1.getDescription()))
                .andExpect(jsonPath("$[0].amount")
                        .value(expense1.getAmount().doubleValue()))
                .andExpect(jsonPath("$[0].category")
                        .value(expense1.getCategory().name()))
                .andExpect(jsonPath("$[0].expenseDate")
                        .value(expense1.getExpenseDate().toString()))

                .andExpect(jsonPath("$[1].id")
                        .value(expense2.getId().intValue()))
                .andExpect(jsonPath("$[1].description")
                        .value(expense2.getDescription()))
                .andExpect(jsonPath("$[1].amount")
                        .value(expense2.getAmount().doubleValue()))
                .andExpect(jsonPath("$[1].category")
                        .value(expense2.getCategory().name()))
                .andExpect(jsonPath("$[1].expenseDate")
                        .value(expense2.getExpenseDate().toString()));

        verify(expenseService, times(1)).getAllExpenses();
    }

    @Test
    @DisplayName("Get expense by ID should return ok")
    void getExpenseById_shouldReturnOk() throws Exception {
        // Given
        ExpenseResponse expense = ExpenseResponse.builder()
                .id(1L)
                .description("Balibago to BGC Bus")
                .amount(new BigDecimal("122.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T10:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .build();

        when(expenseService.getExpenseById(eq(expense.getId()))).thenReturn(expense);

        // When/Then
        mockMvc.perform(
                        get("/api/expenses/{id}", expense.getId().intValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(expense.getId().intValue()))
                .andExpect(jsonPath("$.description")
                        .value(expense.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(expense.getAmount().doubleValue()))
                .andExpect(jsonPath("$.category")
                        .value(expense.getCategory().name()))
                .andExpect(jsonPath("$.expenseDate")
                        .value(expense.getExpenseDate().toString()))
                .andExpect(jsonPath("$.createdAt")
                        .value(expense.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt")
                        .value(expense.getUpdatedAt().toString()));

        verify(expenseService, times(1)).getExpenseById(eq(expense.getId()));
    }

    @Test
    @DisplayName("Get expense by ID when not found should return not found")
    void getExpenseById_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        long nonExistingId = 999L;

        when(expenseService.getExpenseById(eq(nonExistingId)))
                .thenThrow(new ExpenseNotFoundException("Expense not found with ID: " + nonExistingId));

        // When/Then
        mockMvc.perform(
                        get("/api/expenses/{id}", nonExistingId))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).getExpenseById(eq(nonExistingId));
    }

    @Test
    @DisplayName("Create expense with blank description should return bad request")
    void createExpense_withBlankDescription_shouldReturnBadRequest() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "",
                    "amount": "4000.00",
                    "category": "ITEMS",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Create expense with null amount should return bad request")
    void createExpense_withNullAmount_shouldReturnBadRequest() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "category": "ITEMS",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Create expense with amount below minimum should return bad request")
    void createExpense_withAmountBelowMinimum_shouldReturnBadRequest() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "0.00",
                    "category": "ITEMS",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Create expense with missing category should return bad request")
    void createExpense_withMissingCategory_shouldReturnBadRequest() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "4000.00",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(ExpenseRequest.class));
    }

    @Test
    @DisplayName("Create expense with missing expense date should return bad request")
    void createExpense_withExpenseDate_shouldReturnBadRequest() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "4000.00",
                    "category": "ITEMS"
                }
                """;

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(ExpenseRequest.class));
    }
}
