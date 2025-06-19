package com.enjoytrip.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class UserResponseDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private Long accessTokenExpirationTime;
        private Long refreshTokenExpirationTime;
        private UserResponseDto.UserInfo userInfo;

        public void setUserInfo(UserResponseDto.UserInfo userInfo) {
            this.userInfo = userInfo;
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserInfo {
        private Integer mno;
        private String name;
        private String email;
        private String role;
    }
}
