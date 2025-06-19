package com.enjoytrip.service.token;

public interface RefreshTokenService {
    void writeTokenInfo(String email, String refreshToken, String accessToken);
    void removeRefreshToken(String accessToken);
}
