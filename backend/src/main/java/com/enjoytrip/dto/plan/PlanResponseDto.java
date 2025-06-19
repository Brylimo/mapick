package com.enjoytrip.dto.plan;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PlanResponseDto {
	@Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class PlanInfo {
		private int pid;
	    private String name;
	    private int mno;
	    private String memberName;
	    private Date createdDate;
	    private Date updatedDate;
    }
}
