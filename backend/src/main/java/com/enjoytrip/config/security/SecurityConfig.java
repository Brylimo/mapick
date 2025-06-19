package com.enjoytrip.config.security;

import com.enjoytrip.security.service.filter.JwtAuthenticationFilter;
import com.enjoytrip.service.outh.OAuth2SuccessHandler;
import com.enjoytrip.service.outh.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {
    @Value("${app.front.url}")
    private String frontUrl;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic->basic.disable())
                .sessionManagement(configurer->configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorizeRequests->
                        authorizeRequests
                                .requestMatchers(HttpMethod.GET, "/members").permitAll()
                                .requestMatchers(HttpMethod.POST, "/members").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/members").permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"),
                                        new AntPathRequestMatcher("/token/login"),
                                        new AntPathRequestMatcher("/ai/simple"),
                                        new AntPathRequestMatcher("/token/logout"),
                                        new AntPathRequestMatcher("/email/verification-code"),
                                        new AntPathRequestMatcher("/members/verification-code"),
                                        new AntPathRequestMatcher("/community-attractions/popular"),
                                        new AntPathRequestMatcher("/communities/top3")).permitAll() // 공개 경로
                                .requestMatchers(new AntPathRequestMatcher("/token/**"),
                                        new AntPathRequestMatcher("/communities/**"),
                                        new AntPathRequestMatcher("/boards/**"),
                                        new AntPathRequestMatcher("/comment/**"),
                                        new AntPathRequestMatcher("/sub-comment/**"),
                                        new AntPathRequestMatcher("/board-likes/**"),
                                        new AntPathRequestMatcher("/members/**"),
                                        new AntPathRequestMatcher("/notices/**"),
                                        new AntPathRequestMatcher("/community-attractions/**"),
                                        new AntPathRequestMatcher("/plans/**"),
                                        new AntPathRequestMatcher("/tags/**"),
                                        new AntPathRequestMatcher("/plan-expense/**"),
                                		new AntPathRequestMatcher("/plan-attractions/**"))
                                .hasAnyRole("USER", "ADMIN")
                                .anyRequest().authenticated() // 인증 필요
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()) // H2 Console 프레임 보호 비활성화
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(user -> user.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList(frontUrl));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    PasswordEncoder passEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    RoleHierarchy roleHierachy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("ADMIN").implies("USER").role("USER").implies("GUEST").build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
