package com.enjoytrip.service.outh;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.user.UserResponseDto;
import com.enjoytrip.security.dto.CustomOAuth2User;
import com.enjoytrip.security.service.provider.TokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;

    private final ResponseDto responseDto;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Member member = customUser.getMember();

        UserResponseDto.TokenInfo tokenInfo = tokenProvider.generateOAuthTokens(member);

        tokenInfo.setUserInfo(UserResponseDto.UserInfo.builder()
                .mno(member.getMno())
                .name(member.getName())
                .email(member.getEmail())
                .role(member.getRole())
                .build());

        if (tokenInfo == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJsonResponse(response, responseDto.fail("login failed.", HttpStatus.BAD_REQUEST));
            return;
        }

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("access_token", tokenInfo.getAccessToken())
                .path("/")
                .httpOnly(true)
                .maxAge(7 * 24 * 60 * 60 * 1000);

        /*if (cookieSecure) {
            builder = builder.secure(true);
        }*/

        /*if (!cookieSameSite.equals("null") && StringUtils.hasText(cookieSameSite)) {
            builder = builder.sameSite(cookieSameSite);
        }*/

        ResponseCookie cookie = builder.build();
        response.addHeader("Set-Cookie", cookie.toString());

        response.sendRedirect("http://localhost:5173/outh-success");
    }

    private void writeJsonResponse(HttpServletResponse response, ResponseEntity<?> entity) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(entity.getStatusCodeValue());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(entity.getBody());
        response.getWriter().write(json);
    }
}
