package com.isaac.expensetracker.service.impl;

import com.isaac.expensetracker.dto.ExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;
import com.isaac.expensetracker.model.Expense;
import com.isaac.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final AtomicLong expenseCount = new AtomicLong(0);

    private final Map<Long, Expense> expenses = new ConcurrentHashMap<>();

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Long id = expenseCount.incrementAndGet();
        Instant now = Instant.now();

        Expense newExpense = Expense.builder()
                .id(id)
                .description(request.getDescription())
                .amount(request.getAmount())
                .category(request.getCategory())
                .expenseDate(request.getExpenseDate())
                .createdAt(now)
                .updatedAt(now)
                .build();

        expenses.put(id, newExpense);

        return buildResponse(newExpense);
    }

    @Override
    public List<ExpenseResponse> getAllExpenses() {
        return expenses.values().stream()
                .map(this::buildResponse)
                .toList();
    }

    private ExpenseResponse buildResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .expenseDate(expense.getExpenseDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
