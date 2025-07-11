package com.enjoytrip.repository.tokenRepository;

import com.enjoytrip.domain.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>  {
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
