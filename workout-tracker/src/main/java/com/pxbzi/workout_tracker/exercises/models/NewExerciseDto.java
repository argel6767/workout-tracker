package com.pxbzi.workout_tracker.exercises.models;

import java.util.List;

public record NewExerciseDto(String name, String description, List<Long> musclesWorked) {
}
