package com.isaac.expensetracker.controller;

import com.isaac.expensetracker.dto.ExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
import com.isaac.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        List<ExpenseResponse> response = expenseService.getAllExpenses();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        ExpenseResponse response = expenseService.getExpenseById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
