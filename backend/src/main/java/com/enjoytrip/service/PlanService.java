package com.enjoytrip.service;

import com.enjoytrip.model.dto.PlanDTO;

import java.util.List;

public interface PlanService {
    void createPlan(PlanDTO plan);

    PlanDTO getPlan(int pid);

    PlanDTO getPlanByName(String planName, int mno);

    List<PlanDTO> getAllPlans(int mno);

    void updatePlan(PlanDTO plan);

    boolean deletePlan(int mno);
}
