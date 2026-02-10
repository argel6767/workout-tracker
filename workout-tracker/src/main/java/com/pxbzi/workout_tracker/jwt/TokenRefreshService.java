package com.pxbzi.workout_tracker.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.pxbzi.workout_tracker.jwt.JwtService;
import com.pxbzi.workout_tracker.jwt.TokenResponse;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtService jwtService;
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    /**
     * Extracts the refresh token from cookies and generates a new token pair.
     * 
     * @param request The HTTP request containing the refresh_token cookie
     * @return A new TokenResponse with fresh access and refresh tokens
     * @throws IllegalArgumentException if no refresh token found or token is invalid
     */
    public TokenResponse refreshTokens(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new IllegalArgumentException("No refresh token found in cookies");
        }

        // Validate that it's a valid refresh token (not an access token)
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        // Extract the subject (username/user ID) from the refresh token
        String subject = jwtService.extractSubject(refreshToken);

        // Generate a new token pair (both access and refresh for token rotation)
        return jwtService.generateTokenPair(Long.parseLong(subject));
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                String value = cookie.getValue();
                return (value != null && !value.isBlank()) ? value : null;
            }
        }
        return null;
    }
}