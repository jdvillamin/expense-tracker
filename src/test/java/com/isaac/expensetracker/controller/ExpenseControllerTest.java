package com.isaac.expensetracker.controller;

import com.isaac.expensetracker.dto.ExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
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

        List<ExpenseResponse> expectedExpeneses = List.of(
                expense1,
                expense2
        );

        when(expenseService.getAllExpenses()).thenReturn(expectedExpeneses);

        // When/Then
        mockMvc.perform(
                        get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Balibago to BGC Bus"))
                .andExpect(jsonPath("$[0].amount").value(122.00))
                .andExpect(jsonPath("$[0].category").value("TRANSPORTATION"))
                .andExpect(jsonPath("$[0].expenseDate").value("2026-10-18"))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("BGC to Balibago Bus"))
                .andExpect(jsonPath("$[1].amount").value(100.00))
                .andExpect(jsonPath("$[1].category").value("TRANSPORTATION"))
                .andExpect(jsonPath("$[1].expenseDate").value("2026-10-18"));

        verify(expenseService, times(1)).getAllExpenses();
    }
}
