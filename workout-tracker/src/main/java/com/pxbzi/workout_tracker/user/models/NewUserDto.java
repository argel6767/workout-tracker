package com.pxbzi.workout_tracker.user.models;

public record NewUserDto(String email, String username, String hashedPassword) {
}