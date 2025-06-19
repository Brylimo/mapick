package com.enjoytrip.dto.board;

import lombok.*;

import java.util.Date;

public class BoardResponseDto {
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class BoardInfo {
        private long id;
        private String title;
        private String content;
        private int authorId;
        private String authorName;
        private Date createdAt;
        private Date updatedAt;
        private int likeCnt;
        private boolean liked;
    }
}
