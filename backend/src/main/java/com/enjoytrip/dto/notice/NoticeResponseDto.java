package com.enjoytrip.dto.notice;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class NoticeResponseDto {
	@Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class NoticeInfo {
		private long id;
	    private String title;
	    private String content;
	    private int authorId;
	    private String authorName;
	    private Date createdAt;
	    private Date updatedAt;
    }
}
