package com.enjoytrip.security.service.filter;

import com.enjoytrip.domain.member.Member;
import com.enjoytrip.domain.token.RefreshToken;
import com.enjoytrip.model.dao.MemberDAO;
import com.enjoytrip.repository.tokenRepository.RefreshTokenRepository;
import com.enjoytrip.security.service.provider.TokenProvider;
import com.enjoytrip.security.service.service.CustomUserDetailService;
import com.enjoytrip.service.MemberService;
import com.enjoytrip.service.token.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${app.cookie.secure}")
    private boolean cookieSecure;
    @Value("${app.cookie.sameSite:null}")
    private String cookieSameSite;

    private final CustomUserDetailService customUserDetailService;
    private final RefreshTokenService refreshTokenService;

    private final MemberDAO memberDAO;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseCookieToken(request);

            if (StringUtils.hasText(token) && !token.equalsIgnoreCase("null")) {
                if (tokenProvider.validateToken(token)) {
                    String email = tokenProvider.validateAndGetUsername(token);
                    Member user = customUserDetailService.loadUserByUsername(email);

                    AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(securityContext);
                } else {
                    RefreshToken rtkInfo = refreshTokenRepository.findByAccessToken(token)
                            .orElseThrow(()-> new RuntimeException("access token not found"));
                    String refreshToken = rtkInfo.getRefreshToken();

                    if (tokenProvider.validateToken(refreshToken)) {
                        String email = rtkInfo.getEmail();

                        Member user = memberDAO.getMemberByEmail(email);
                        String newAccessToken = tokenProvider.generateAccessToken(user);
                        refreshTokenService.writeTokenInfo(email, refreshToken, newAccessToken);

                        AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        securityContext.setAuthentication(authenticationToken);
                        SecurityContextHolder.setContext(securityContext);

                        ResponseCookie.ResponseCookieBuilder builder  = ResponseCookie.from("access_token", newAccessToken)
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
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    // extract token info
    private String parseCookieToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            Cookie atkCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("access_token")).findFirst().orElse(null);
            if (atkCookie != null) {
                String token = atkCookie.getValue();
                return token;
            }
        }
        return null;
    }
}
