package com.enjoytrip.model.dto;

import java.util.Date;
import java.util.List;

import com.enjoytrip.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDTO {
	private int communityId;
	private String communityName;
	private int registeredBy;
	private String desc;
	private Date regDt;
	private Date modDt;

	private Member member;
	private List<CategoryDTO> categoryList;
	private List<TagDTO> tagList;
}
