package com.enjoytrip.service;

import com.enjoytrip.model.dto.PlanExpenseDTO;

import java.util.List;

public interface PlanExpenseService {
    void addExpense(PlanExpenseDTO planExpensedto);

    PlanExpenseDTO getExpenseById(int expId);

    List<PlanExpenseDTO> getExpensesByPlanId(int planId);

    void deleteExpenseById(int expId);

    void updateReceiptPath(int expId, String path);

    String getReceiptPath(int expId);
}
