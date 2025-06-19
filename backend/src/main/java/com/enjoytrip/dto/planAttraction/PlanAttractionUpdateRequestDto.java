package com.enjoytrip.dto.planAttraction;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PlanAttractionUpdateRequestDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlanAttractionUdapteInfo {
    	private int ppid;
        private String exp;
        private LocalTime time;
    }
}
