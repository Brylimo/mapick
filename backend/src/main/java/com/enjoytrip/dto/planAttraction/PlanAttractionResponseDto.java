package com.enjoytrip.dto.planAttraction;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PlanAttractionResponseDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlanAttractionInfo {
    	private int ppid;
        private int idx;
        private int planid;
        private int attractionid;
        private String attractionName;
        private String exp;
    }
}
