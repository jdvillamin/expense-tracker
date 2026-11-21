package com.isaac.expensetracker.service;

import com.isaac.expensetracker.dto.ExpenseRequest;
import com.isaac.expensetracker.dto.ExpenseResponse;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse createExpense(ExpenseRequest request);

    List<ExpenseResponse> getAllExpenses();

    ExpenseResponse getExpenseById(Long id);
}
