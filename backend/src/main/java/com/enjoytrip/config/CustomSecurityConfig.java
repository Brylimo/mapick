package com.enjoytrip.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import jakarta.servlet.http.Cookie;

//@Configuration
//@EnableWebSecurity
public class CustomSecurityConfig {
	/*@Bean
	RoleHierarchy roleHierachy() {
		return RoleHierarchyImpl.withDefaultRolePrefix()
				.role("ADMIN").implies("USER").role("USER").implies("GUEST").build();
	}
	@Bean
	PasswordEncoder passEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	@Bean
	SecurityFilterChain normalFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
		http.csrf(csrf->csrf.disable());
		http.logout(customizer -> customizer.logoutUrl("/members/logout")
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID", "email"));
		http.formLogin(customizer -> customizer.loginPage("/members/login")
												.loginProcessingUrl("/members/login")
												.successHandler((req, res, auth) ->{
													String rememberId = req.getParameter("rememberId");
													if(rememberId!=null) {
														Cookie cookie = new Cookie("email", auth.getName());
														cookie.setMaxAge(9999);
														res.addCookie(cookie);
													}else {
														Cookie[] cookies = req.getCookies();
														if (cookies != null) {
												            for (Cookie cookie : cookies) {
												                if ("email".equals(cookie.getName())) {
												                    cookie.setMaxAge(0);
												                    res.addCookie(cookie);
												                }
												            }
												        }
													}
													res.sendRedirect(req.getContextPath()+"/");
												})
												.usernameParameter("email")
												.failureUrl("/members/loginForm?error"));
		http.rememberMe(customizer -> customizer.tokenValiditySeconds(60*1)
												.userDetailsService(userDetailsService));
		return http.build();
	}*/

}
