package com.pxbzi.workout_tracker.jwt;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    @Override
    protected void doFilterInternal(
        @Nonnull HttpServletRequest request,
        @Nonnull HttpServletResponse response,
        @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractTokenFromCookies(request);

        // No access_token cookie present – continue unauthenticated
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Only accept access tokens – reject refresh tokens in normal requests
            if (
                !jwtService.isTokenValid(token) ||
                !jwtService.isAccessToken(token)
            ) {
                filterChain.doFilter(request, response);
                return;
            }

            String subject = jwtService.extractSubject(token);

            // Only set authentication if the security context is empty (not already authenticated)
            if (
                subject != null &&
                SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        subject,
                        null,
                        Collections.emptyList()
                    );
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Token is malformed / expired / tampered – proceed unauthenticated
            logger.debug("JWT authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                String value = cookie.getValue();
                return (value != null && !value.isBlank()) ? value : null;
            }
        }
        return null;
    }
}
