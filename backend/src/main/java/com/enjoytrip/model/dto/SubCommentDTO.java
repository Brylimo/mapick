package com.enjoytrip.model.dto;

import java.util.Date;

import com.enjoytrip.domain.member.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCommentDTO {
	private int subCommentId;
	private int commentId;
	private int memberId;
	private String memberName;
	private String content;
	private Date regDate;
	private Date modDate;
	private Member member;
}
