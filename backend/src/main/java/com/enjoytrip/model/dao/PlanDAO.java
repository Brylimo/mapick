package com.enjoytrip.model.dao;

import com.enjoytrip.model.dto.PlanDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanDAO {
    void insertPlan(PlanDTO plan);

    PlanDTO getPlan(int pid);

    PlanDTO getPlanByName(String planName, int mno);

    List<PlanDTO> getAllPlan(int mno);

    void updatePlan(PlanDTO plan);

    void deletePlan(int mno);
}
