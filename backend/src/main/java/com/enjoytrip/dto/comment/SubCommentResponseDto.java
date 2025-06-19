package com.enjoytrip.dto.comment;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SubCommentResponseDto {
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class SubCommentInfo{
		private int subCommentId;
		private long commentId;
		private int memberId;
		private String memberName;
		private String content;
		private Date regDate;
		private Date modDate;
	}
}
