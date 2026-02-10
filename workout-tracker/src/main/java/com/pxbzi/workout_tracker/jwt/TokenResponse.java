package com.pxbzi.workout_tracker.jwt;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {}
