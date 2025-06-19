package com.enjoytrip.service;

import com.enjoytrip.model.dto.PlanAttractionDTO;

import java.util.List;

public interface PlanAttractionService {
    PlanAttractionDTO createPlanAttraction(PlanAttractionDTO planAttraction);
    List<PlanAttractionDTO> getAllPlanAttractions(Integer pid);
    boolean planAttractionDelete(int planAttractionId);
	PlanAttractionDTO getPlanAttractionById(int ppid);
	void updatePlanAttraction(PlanAttractionDTO planAttraction);
}
