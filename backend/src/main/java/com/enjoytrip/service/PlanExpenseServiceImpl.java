package com.enjoytrip.service;

import com.enjoytrip.model.dao.PlanExpenseDAO;
import com.enjoytrip.model.dto.PlanExpenseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanExpenseServiceImpl implements PlanExpenseService{
    private final PlanExpenseDAO planExpenseDao;

    @Override
    public void addExpense(PlanExpenseDTO planExpensedto) {
        planExpenseDao.addExpense(planExpensedto);
    }

    @Override
    public PlanExpenseDTO getExpenseById(int expId) {
        return planExpenseDao.getExpenseById(expId);
    }

    @Override
    public List<PlanExpenseDTO> getExpensesByPlanId(int planId) {
        return planExpenseDao.getExpensesByPlanId(planId);
    }

    @Override
    public void deleteExpenseById(int expId) {
        planExpenseDao.deleteExpenseById(expId);
    }

    @Override
    public void updateReceiptPath(int expId, String path) {
        planExpenseDao.updateReceiptPath(expId, path);
    }

    @Override
    public String getReceiptPath(int expId) {
        return planExpenseDao.getReceiptPath(expId);
    }
}
