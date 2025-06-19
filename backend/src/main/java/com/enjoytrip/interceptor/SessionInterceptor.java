package com.enjoytrip.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("userId") != null) { // todo loggedInUser로 변경 필요
            return true;
        } else {
            session.setAttribute("alertMsg", "로그인 후 사용하세요.");
            response.sendRedirect(request.getContextPath() + "/members/loginForm");
            return false;
        }
    }
}

