package com.enjoytrip.service;

import com.enjoytrip.model.dao.PlanDAO;
import com.enjoytrip.model.dto.PlanDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    private final PlanDAO planDAO;

    @Override
    @Transactional
    public void createPlan(PlanDTO plan) {
        planDAO.insertPlan(plan);
    }

    @Override
    @Transactional
    public PlanDTO getPlan(int pid) {
        return null;
    }

    @Override
    @Transactional
    public PlanDTO getPlanByName(String planName, int mno) {
        return planDAO.getPlanByName(planName, mno);
    }

    @Override
    @Transactional
    public List<PlanDTO> getAllPlans(int mno) {
        return planDAO.getAllPlan(mno);
    }

    @Override
    @Transactional
    public void updatePlan(PlanDTO plan) {

    }

    @Override
    @Transactional
    public boolean deletePlan(int mno) {
        return false;
    }
}
