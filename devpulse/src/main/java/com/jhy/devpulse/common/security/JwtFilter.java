package com.jhy.devpulse.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jhy.devpulse.common.exception.CustomException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader(JwtProvider.AUTHORIZATION_HEADER);

        if (bearerToken != null) {

            String token = jwtProvider.resolveToken(bearerToken);

            try {

                Authentication authentication = jwtProvider.getAuthentication(token);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (CustomException e) {

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("""
                        {
                          "message": "%s"
                        }
                        """.formatted(e.getErrorCode().getMessage()));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
