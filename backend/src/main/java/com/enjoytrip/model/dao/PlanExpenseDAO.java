package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.PlanExpenseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanExpenseDAO {
    void addExpense(PlanExpenseDTO planExpensedto);

    PlanExpenseDTO getExpenseById(int expId);

    List<PlanExpenseDTO> getExpensesByPlanId(int planId);

    void deleteExpenseById(int expId);

    void updateReceiptPath(int expId, String path);

    String getReceiptPath(int expId);
}
