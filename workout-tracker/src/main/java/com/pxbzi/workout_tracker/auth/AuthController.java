package com.pxbzi.workout_tracker.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

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

import org.springframework.web.bind.annotation.RequestBody;
import com.pxbzi.workout_tracker.user.UserDto;
import com.pxbzi.workout_tracker.auth.models.AuthenticationDto;
import com.pxbzi.workout_tracker.auth.models.RegistrationDto;

@RestController
@RequestMapping("/auth")
@Data
public class AuthController {

    private final JwtService jwtService;
    private final CookieService cookieService;
    private final TokenRefreshService tokenRefreshService;
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<UserDto> register(@RequestBody RegistrationDto registrationDto) {
        UserDto newUser = authService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(newUser);
    }

    /**
     * 
     * POST /auth/login with credentials
     * Returns access_token and refresh_token as HttpOnly cookies
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody AuthenticationDto authenticationDto) {
        UserDto userDto = authService.authenticateUser(authenticationDto);
        TokenResponse tokens = jwtService.generateTokenPair(userDto.id(), UserDto.dtoToClaims(userDto));

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(tokens.accessToken()).toString())
            .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(tokens.refreshToken()).toString())
            .body(userDto);
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