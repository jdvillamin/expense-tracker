package com.isaac.expensetracker.dto;

import com.isaac.expensetracker.model.ExpenseCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private String description;
    private BigDecimal amount;
    private ExpenseCategory category;
    private LocalDate expenseDate;
    private Instant createdAt;
    private Instant updatedAt;
}
