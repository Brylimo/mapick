package com.enjoytrip.dto.notice;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class NoticeRequestDto {
	@Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class NoticeRegist {
	    private String title;
	    private String content;
    }
}
