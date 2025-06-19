package com.enjoytrip.model.dto;

import java.time.LocalDateTime;
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
public class BoardDTO {
	private long id;
	private int communityId;
	private String title;
	private String content;
	private int author;
	private Date createdAt;
	private Date updatedAt;
	private transient LocalDateTime reservedTime;

	private Member member;

	public BoardDTO(BoardDTO boardDTO) {
		this.id = boardDTO.id;
		this.communityId = boardDTO.communityId;
		this.title = boardDTO.title;
		this.content = boardDTO.content;
		this.author = boardDTO.author;
		this.reservedTime = boardDTO.reservedTime;
	}

}
