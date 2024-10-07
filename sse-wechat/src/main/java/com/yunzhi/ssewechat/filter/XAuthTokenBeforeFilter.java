package com.yunzhi.ssewechat.filter;

import com.yunzhi.ssewechat.entity.User;
import com.yunzhi.ssewechat.model.ExpiredHashMap;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class XAuthTokenBeforeFilter extends OncePerRequestFilter {
    public static ExpiredHashMap<String, User> map = new ExpiredHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String xAuthToken = request.getHeader("x-auth-token");
        if (xAuthToken != null && !xAuthToken.isEmpty()) {
            User user = map.get(xAuthToken);
            if (user != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                map.remove(xAuthToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}