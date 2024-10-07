package com.yunzhi.ssewechat.filter;

import com.yunzhi.ssewechat.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class XAuthTokenAfterFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String xAuthToken = request.getHeader("x-auth-token");
            if (xAuthToken == null || xAuthToken.isEmpty()) {
                xAuthToken = UUID.randomUUID().toString();
            }
            XAuthTokenBeforeFilter.map.put(xAuthToken, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            response.addHeader("x-auth-token", xAuthToken);
        }
        filterChain.doFilter(request, response);
    }
}
