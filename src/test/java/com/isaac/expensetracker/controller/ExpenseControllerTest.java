package com.isaac.expensetracker.controller;

import com.isaac.expensetracker.dto.CreateExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
import com.isaac.expensetracker.dto.UpdateExpenseRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        ExpenseResponse expectedExpense = ExpenseResponse.builder()
                .id(1L)
                .description("Balibago to BGC Bus")
                .amount(new BigDecimal("122.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T10:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .build();

        when(expenseService.createExpense(any(CreateExpenseRequest.class))).thenReturn(expectedExpense);

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(expectedExpense.getId().intValue()))
                .andExpect(jsonPath("$.description")
                        .value(expectedExpense.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(expectedExpense.getAmount().doubleValue()))
                .andExpect(jsonPath("$.category")
                        .value(expectedExpense.getCategory().name()))
                .andExpect(jsonPath("$.expenseDate")
                        .value(expectedExpense.getExpenseDate().toString()))
                .andExpect(jsonPath("$.createdAt")
                        .value(expectedExpense.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt")
                        .value(expectedExpense.getUpdatedAt().toString()));

        verify(expenseService).createExpense(any(CreateExpenseRequest.class));
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

        verify(expenseService).getAllExpenses();
    }

    @Test
    @DisplayName("Get expense by ID should return ok")
    void getExpenseById_shouldReturnOk() throws Exception {
        // Given
        ExpenseResponse expectedExpense = ExpenseResponse.builder()
                .id(1L)
                .description("Balibago to BGC Bus")
                .amount(new BigDecimal("122.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T10:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .build();

        when(expenseService.getExpenseById(eq(expectedExpense.getId()))).thenReturn(expectedExpense);

        // When/Then
        mockMvc.perform(
                        get("/api/expenses/{id}", expectedExpense.getId().intValue()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(expectedExpense.getId().intValue()))
                .andExpect(jsonPath("$.description")
                        .value(expectedExpense.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(expectedExpense.getAmount().doubleValue()))
                .andExpect(jsonPath("$.category")
                        .value(expectedExpense.getCategory().name()))
                .andExpect(jsonPath("$.expenseDate")
                        .value(expectedExpense.getExpenseDate().toString()))
                .andExpect(jsonPath("$.createdAt")
                        .value(expectedExpense.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt")
                        .value(expectedExpense.getUpdatedAt().toString()));

        verify(expenseService).getExpenseById(eq(expectedExpense.getId()));
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

        verify(expenseService).getExpenseById(eq(nonExistingId));
    }

    @Test
    @DisplayName("Update expense should return ok")
    void updateExpense_shouldReturnOk() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "Balibago to BGC Bus",
                    "amount": 122.00,
                    "category": "TRANSPORTATION",
                    "expenseDate": "2026-10-18"
                }
                """;

        ExpenseResponse expectedExpense = ExpenseResponse.builder()
                .id(1L)
                .description("Balibago to BGC Bus")
                .amount(new BigDecimal("122.00"))
                .category(ExpenseCategory.TRANSPORTATION)
                .expenseDate(LocalDate.of(2026, 10, 18))
                .createdAt(Instant.parse("2026-10-18T10:00:00Z"))
                .updatedAt(Instant.parse("2026-10-18T10:00:00Z"))
                .build();

        when(expenseService.updateExpense(eq(expectedExpense.getId()), any(UpdateExpenseRequest.class)))
                .thenReturn(expectedExpense);

        // When/Then
        mockMvc.perform(
                        put("/api/expenses/{id}", expectedExpense.getId().intValue())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(expectedExpense.getId().intValue()))
                .andExpect(jsonPath("$.description")
                        .value(expectedExpense.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(expectedExpense.getAmount().doubleValue()))
                .andExpect(jsonPath("$.category")
                        .value(expectedExpense.getCategory().name()))
                .andExpect(jsonPath("$.expenseDate")
                        .value(expectedExpense.getExpenseDate().toString()))
                .andExpect(jsonPath("$.createdAt")
                        .value(expectedExpense.getCreatedAt().toString()))
                .andExpect(jsonPath("$.updatedAt")
                        .value(expectedExpense.getUpdatedAt().toString()));

        verify(expenseService).updateExpense(eq(expectedExpense.getId()), any(UpdateExpenseRequest.class));
    }

    @Test
    @DisplayName("Update expense when not found should return not found")
    void updateExpense_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        Long nonExistingId = 999L;
        String jsonRequest = """
                {
                    "description": "Balibago to BGC Bus",
                    "amount": 122.00,
                    "category": "TRANSPORTATION",
                    "expenseDate": "2026-10-18"
                }
                """;

        when(expenseService.updateExpense(eq(nonExistingId), any(UpdateExpenseRequest.class)))
                .thenThrow(new ExpenseNotFoundException("Expense not found with ID: " + nonExistingId));

        // When/Then
        mockMvc.perform(
                        put("/api/expenses/{id}", nonExistingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isNotFound());

        verify(expenseService).updateExpense(eq(nonExistingId), any(UpdateExpenseRequest.class));
    }

    @Test
    @DisplayName("Delete expense should return no content")
    void deleteExpense_shouldReturnNoContent() throws Exception {
        // Given
        Long id = 1L;

        doNothing().when(expenseService).deleteExpense(id);

        // When/Then
        mockMvc.perform(
                        delete("/api/expenses/{id}", id))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense(eq(id));
    }

    @Test
    @DisplayName("Delete expense when not found should return not found")
    void deleteExpense_whenNotFound_shouldReturnNotFound() throws Exception {
        // Given
        Long nonExistingId = 999L;

        doThrow(new ExpenseNotFoundException("Expense not found using ID: " + nonExistingId))
                .when(expenseService).deleteExpense(nonExistingId);

        // When/Then
        mockMvc.perform(
                        delete("/api/expenses/{id}", nonExistingId))
                .andExpect(status().isNotFound());

        verify(expenseService).deleteExpense(nonExistingId);
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

        verify(expenseService, never()).createExpense(any(CreateExpenseRequest.class));
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

        verify(expenseService, never()).createExpense(any(CreateExpenseRequest.class));
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

        verify(expenseService, never()).createExpense(any(CreateExpenseRequest.class));
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

        verify(expenseService, never()).createExpense(any(CreateExpenseRequest.class));
    }

    @Test
    @DisplayName("Create expense with invalid category should return bad request")
    void createExpense_withInvalidCategory_shouldReturnBadRequest() throws Exception {
        // Given
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "4000.00",
                    "category": "NON_EXISTING",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        post("/api/expenses")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).createExpense(any(CreateExpenseRequest.class));
    }

    @Test
    @DisplayName("Create expense with missing expense date should return bad request")
    void createExpense_withMissingExpenseDate_shouldReturnBadRequest() throws Exception {
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

        verify(expenseService, never()).createExpense(any(CreateExpenseRequest.class));
    }

    @Test
    @DisplayName("Update expense with null amount should return bad request")
    void updateExpense_withNullAmount_shouldReturnBadRequest() throws Exception {
        // Given
        Long id = 1L;
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "category": "ITEMS",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        put("/api/expenses/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).updateExpense(eq(id), any(UpdateExpenseRequest.class));
    }

    @Test
    @DisplayName("Update expense with amount below minimum should return bad request")
    void updateExpense_withAmountBelowMinimum_shouldReturnBadRequest() throws Exception {
        // Given
        Long id = 1L;
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
                        put("/api/expenses/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).updateExpense(eq(id), any(UpdateExpenseRequest.class));
    }

    @Test
    @DisplayName("Update expense with missing category should return bad request")
    void updateExpense_withMissingCategory_shouldReturnBadRequest() throws Exception {
        // Given
        Long id = 1L;
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "4000.00",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        put("/api/expenses/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).updateExpense(eq(id), any(UpdateExpenseRequest.class));
    }

    @Test
    @DisplayName("Update expense with invalid category should return bad request")
    void updateExpense_withInvalidCategory_shouldReturnBadRequest() throws Exception {
        // Given
        Long id = 1L;
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "4000.00",
                    "category": "NON_EXISTING",
                    "expenseDate": "2026-11-21"
                }
                """;

        // When/Then
        mockMvc.perform(
                        put("/api/expenses/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).updateExpense(eq(id), any(UpdateExpenseRequest.class));
    }

    @Test
    @DisplayName("Update expense with missing expense date should return bad request")
    void updateExpense_withMissingExpenseDate_shouldReturnBadRequest() throws Exception {
        // Given
        Long id = 1L;
        String jsonRequest = """
                {
                    "description": "GF Gift",
                    "amount": "4000.00",
                    "category": "ITEMS"
                }
                """;

        // When/Then
        mockMvc.perform(
                        put("/api/expenses/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonRequest))
                .andExpect(status().isBadRequest());

        verify(expenseService, never()).updateExpense(eq(id), any(UpdateExpenseRequest.class));
    }
}
