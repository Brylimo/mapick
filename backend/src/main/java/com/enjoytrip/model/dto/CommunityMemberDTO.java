package com.enjoytrip.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMemberDTO {
	private int communityMemberId;
	private int memberId;
	private int communityId;
	private Date regDt;
	private Date modDt;
}
