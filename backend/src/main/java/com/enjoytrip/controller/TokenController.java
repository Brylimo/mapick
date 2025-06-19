package com.enjoytrip.controller;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.dto.user.UserRequestDto;
import com.enjoytrip.dto.ResponseDto;
import com.enjoytrip.dto.user.UserResponseDto;
import com.enjoytrip.service.MemberService;
import com.enjoytrip.service.token.RefreshTokenService;
import com.enjoytrip.util.ErrorUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/token")
public class TokenController {
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;
    @Value("${app.cookie.sameSite:null}")
    private String cookieSameSite;

    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final ErrorUtil errorUtil;

    private final ResponseDto responseDto;

    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping(value="/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Validated UserRequestDto.Login userLoginRequestDto, HttpServletResponse response, Errors errors) {
        if (errors.hasErrors()) {
            return responseDto.invalidData(errorUtil.flatErrors(errors));
        }

        try {
        	
            UserResponseDto.TokenInfo tokenInfo = memberService.login(userLoginRequestDto);

            if (tokenInfo == null) {
                return responseDto.fail("login failed.", HttpStatus.BAD_REQUEST);
            }

            ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("access_token", tokenInfo.getAccessToken())
                    .path("/")
                    .httpOnly(true)
                    .maxAge(7 * 24 * 60 * 60 * 1000);

            if (cookieSecure) {
                builder = builder.secure(true);
            }

            /*if (!cookieSameSite.equals("null") && StringUtils.hasText(cookieSameSite)) {
                builder = builder.sameSite(cookieSameSite);
            }*/

            ResponseCookie cookie = builder.build();
            response.addHeader("Set-Cookie", cookie.toString());

            String readableRole = switch (tokenInfo.getUserInfo().getRole()) {
                case "ROLE_ADMIN" -> "관리자";
                case "ROLE_USER" -> "일반 사용자";
                default -> "알 수 없음";
            };

            // 권한 정보 변경
            tokenInfo.getUserInfo().setRole(readableRole);
            return responseDto.success(tokenInfo.getUserInfo());
        } catch (Exception e) {
            log.debug("login error occurred!");
            return responseDto.fail("server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @GetMapping(value="/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> logout(
            @Parameter(description = "엑세스 토큰")
            @CookieValue(name="access_token", required = false) String atkCookieValue,
            HttpServletResponse response) {
        if (atkCookieValue != null) {
            refreshTokenService.removeRefreshToken(atkCookieValue);

            ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("access_token", atkCookieValue)
                    .maxAge(Duration.ZERO)
                    .path("/")
                    .httpOnly(true);

            if (cookieSecure) {
                builder = builder.secure(true);
            }

            /*if (!cookieSameSite.equals("null") && StringUtils.hasText(cookieSameSite)) {
                builder = builder.sameSite(cookieSameSite);
            }*/

            ResponseCookie atkCookie = builder.build();
            response.addHeader("Set-Cookie", atkCookie.toString());

            return responseDto.success("logout success ");
        }

        return responseDto.fail("logout failed", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "검증", description = "현재 로그인되어 있는 상태인지를 확인합니다.")
    @GetMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validate(@AuthenticationPrincipal Member member) {
        String readableRole = switch (member.getRole()) {
            case "ROLE_ADMIN" -> "관리자";
            case "ROLE_USER" -> "일반 사용자";
            default -> "알 수 없음";
        };

        return responseDto.success(UserResponseDto.UserInfo.builder()
                .mno(member.getMno())
                .name(member.getName())
                .email(member.getEmail())
                .role(readableRole)
                .build());
    }
}
