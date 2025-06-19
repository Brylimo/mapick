package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.PlanAttractionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanAttractionDAO {
    void insertPlanAttraction(PlanAttractionDTO planAttraction);
    PlanAttractionDTO getPlanAttractionById(int id);
    List<PlanAttractionDTO> getAllPlanAttractions(Integer pid);
    void deletePlanAttraction(int pid);
	void updatePlanAttraction(PlanAttractionDTO planAttraction);
}