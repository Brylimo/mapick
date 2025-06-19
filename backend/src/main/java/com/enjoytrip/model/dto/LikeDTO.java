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
public class LikeDTO {
	private int likeId;
	private int memberId;
	private int boardId;
	private Date regDt;
	private Date modDt;
}
