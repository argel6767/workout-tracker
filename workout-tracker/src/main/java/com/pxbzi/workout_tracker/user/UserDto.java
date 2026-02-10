package com.pxbzi.workout_tracker.user;

import java.util.List;

import com.pxbzi.workout_tracker.user.models.User;
import com.pxbzi.workout_tracker.weights.models.Weight;
import com.pxbzi.workout_tracker.workouts.models.Workout;

import java.util.Map;

public record UserDto(Long id, String username, String email, List<Long> workouts, List<Long> weights) {
    
    public static UserDto getUserDto(User user) {
        List<Long> workoutIds = user.getWorkouts().stream().map(Workout::getId).toList();
        List<Long> weightIds = user.getWeightEntries().stream().map(Weight::getId).toList();
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), workoutIds, weightIds);
    }
    
    public static Map<String, Object> dtoToClaims(UserDto userDto) {
        return Map.of(
            "id", userDto.id(),
            "username", userDto.username(),
            "email", userDto.email()
        );
    }
}