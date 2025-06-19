package com.enjoytrip.domain.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value="rtk", timeToLive = 60*60*24*7)
@ToString
public class RefreshToken {
    @Id
    private String email;
    private String refreshToken;

    @Indexed
    private String accessToken;

    @Builder
    public RefreshToken(String email, String refreshToken, String accessToken) {
        this.email = email;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
