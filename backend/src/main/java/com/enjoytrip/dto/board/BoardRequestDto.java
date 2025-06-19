package com.enjoytrip.dto.board;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.*;

public class BoardRequestDto {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardCreate {
        private String title;
        private String content;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardUpdate {
        private Long boardId;
        private String title;
        private String content;
    }
}
