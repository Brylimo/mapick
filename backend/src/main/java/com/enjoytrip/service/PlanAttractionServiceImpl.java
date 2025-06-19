package com.enjoytrip.service;

import com.enjoytrip.model.dao.PlanAttractionDAO;
import com.enjoytrip.model.dto.PlanAttractionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanAttractionServiceImpl implements PlanAttractionService{
    private final PlanAttractionDAO planAttractionDAO;

    @Override
    @Transactional
    public PlanAttractionDTO createPlanAttraction(PlanAttractionDTO planAttraction) {
        planAttractionDAO.insertPlanAttraction(planAttraction);

        return planAttraction;
    }

    @Override
    @Transactional
    public List<PlanAttractionDTO> getAllPlanAttractions(Integer pid) {
        return planAttractionDAO.getAllPlanAttractions(pid);
    }

    @Override
    @Transactional
    public boolean planAttractionDelete(int planAttractionId) {
        PlanAttractionDTO planAttraction = planAttractionDAO.getPlanAttractionById(planAttractionId);
        if (planAttraction != null) {
            planAttractionDAO.deletePlanAttraction(planAttractionId);
            return true;
        }

        return false;
    }

	@Override
	public PlanAttractionDTO getPlanAttractionById(int ppid) {
		return planAttractionDAO.getPlanAttractionById(ppid);
	}

	@Override
	public void updatePlanAttraction(PlanAttractionDTO planAttraction) {
		planAttractionDAO.updatePlanAttraction(planAttraction);
	}

}
