package com.pxbzi.workout_tracker.jwt;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final JwtService jwtService;

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    @Value("${cookie.secure:false}")
    private boolean secureCookie;

    @Value("${cookie.domain:}")
    private String cookieDomain;

    // ── Build Token Cookies ────────────────────────────────────────────

    public ResponseCookie createAccessTokenCookie(String token) {
        return buildCookie(
            ACCESS_TOKEN_COOKIE,
            token,
            Duration.ofMillis(jwtService.getAccessTokenExpirationMs()),
            "/"
        );
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return buildCookie(
            REFRESH_TOKEN_COOKIE,
            token,
            Duration.ofMillis(jwtService.getRefreshTokenExpirationMs()),
            "/auth"
        );
    }

    // ── Clear Cookies (for logout) ─────────────────────────────────────

    public ResponseCookie clearAccessTokenCookie() {
        return buildCookie(ACCESS_TOKEN_COOKIE, "", Duration.ZERO, "/");
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return buildCookie(REFRESH_TOKEN_COOKIE, "", Duration.ZERO, "/auth");
    }

    // ── Internal Helper ────────────────────────────────────────────────

    private ResponseCookie buildCookie(
        String name,
        String value,
        Duration maxAge,
        String path
    ) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
            .from(name, value)
            .httpOnly(true)
            .secure(secureCookie)
            .path(path)
            .maxAge(maxAge)
            .sameSite("Lax");

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder = builder.domain(cookieDomain);
        }

        return builder.build();
    }
}
