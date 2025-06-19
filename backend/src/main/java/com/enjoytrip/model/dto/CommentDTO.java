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
public class CommentDTO {
	private int commentId;
	private long boardId;
	private int memberId;
	private String content;
	private Date regDate;
	private Date modDate;
	private Member member;
	private List<SubCommentDTO> childComments;
}
