package com.pxbzi.workout_tracker.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Token Generation ───────────────────────────────────────────────

    public String generateAccessToken(Long subject) {
        return buildToken(
            subject,
            Map.of("type", "access"),
            accessTokenExpiration
        );
    }

    public String generateAccessToken(
        Long subject,
        Map<String, Object> extraClaims
    ) {
        var claims = new java.util.HashMap<>(extraClaims);
        claims.put("type", "access");
        return buildToken(subject, claims, accessTokenExpiration);
    }

    public String generateRefreshToken(Long subject) {
        return buildToken(
            subject,
            Map.of("type", "refresh"),
            refreshTokenExpiration
        );
    }

    public TokenResponse generateTokenPair(Long subject) {
        return new TokenResponse(
            generateAccessToken(subject),
            generateRefreshToken(subject)
        );
    }

    public TokenResponse generateTokenPair(
        Long subject,
        Map<String, Object> extraClaims
    ) {
        return new TokenResponse(
            generateAccessToken(subject, extraClaims),
            generateRefreshToken(subject)
        );
    }

    // ── Token Validation ───────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractSubject(token);
            return (
                username.equals(userDetails.getUsername()) &&
                !isTokenExpired(token)
            );
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        try {
            return "access".equals(
                extractClaim(token, c -> c.get("type", String.class))
            );
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return "refresh".equals(
                extractClaim(token, c -> c.get("type", String.class))
            );
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // ── Claim Extraction ───────────────────────────────────────────────

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // ── Internal Helpers ───────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // ── Expiration Getters (used by CookieService for Max-Age) ─────────

    public long getAccessTokenExpirationMs() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpiration;
    }

    private String buildToken(
        Long subject,
        Map<String, Object> claims,
        long expirationMs
    ) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .claims(claims)
            .subject(String.valueOf(subject))
            .issuedAt(new Date(now))
            .expiration(new Date(now + expirationMs))
            .signWith(signingKey)
            .compact();
    }
}
