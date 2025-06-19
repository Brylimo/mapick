package com.enjoytrip.service.token;

import com.enjoytrip.domain.token.RefreshToken;
import com.enjoytrip.repository.tokenRepository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void writeTokenInfo(String email, String refreshToken, String accessToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .email(email)
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .build());
    }

    @Override
    @Transactional
    public void removeRefreshToken(String accessToken) {
        refreshTokenRepository.findByAccessToken(accessToken)
                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
    }
}
