package com.isaac.expensetracker.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    private Long id;
    private String description;
    private BigDecimal amount;
    private ExpenseCategory category;
    private LocalDate expenseDate;
    private Instant createdAt;
    private Instant updatedAt;
}
