package com.enjoytrip.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class EmailRequestDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendCode {
        private String email;
    }
}
