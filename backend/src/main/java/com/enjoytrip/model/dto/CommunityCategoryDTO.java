package com.enjoytrip.model.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCategoryDTO {
	private int communityCategoryId;
	private int communityId;
	private int categoryId;
	private Date regDt;
	private Date modDt;

}
