package com.pxbzi.workout_tracker.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pxbzi.workout_tracker.jwt.CookieService;
import com.pxbzi.workout_tracker.jwt.JwtService;
import com.pxbzi.workout_tracker.jwt.TokenRefreshService;
import com.pxbzi.workout_tracker.jwt.TokenResponse;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final TokenRefreshService tokenRefreshService;

    /**
     * Example login endpoint - you'll need to implement actual authentication
     * 
     * POST /auth/login with credentials
     * Returns access_token and refresh_token as HttpOnly cookies
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(/* Add your LoginRequest DTO here */) {
        // TODO: Validate credentials against your user database
        // TODO: Get username from authenticated user
        String username = "exampleUser"; // Replace with actual username
        
        TokenResponse tokens = jwtService.generateTokenPair(username);

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(tokens.accessToken()).toString())
            .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(tokens.refreshToken()).toString())
            .body("Login successful");
    }

    /**
     * Refresh token endpoint
     * 
     * POST /auth/refresh (with refresh_token cookie)
     * Returns new access_token and refresh_token cookies
     */
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request) {
        try {
            TokenResponse tokens = tokenRefreshService.refreshTokens(request);

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(tokens.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(tokens.refreshToken()).toString())
                .body("Tokens refreshed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Logout endpoint - clears both cookies
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookieService.clearAccessTokenCookie().toString())
            .header(HttpHeaders.SET_COOKIE, cookieService.clearRefreshTokenCookie().toString())
            .body("Logged out successfully");
    }
}