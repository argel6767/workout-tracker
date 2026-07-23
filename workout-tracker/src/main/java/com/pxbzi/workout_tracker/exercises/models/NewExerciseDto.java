package com.pxbzi.workout_tracker.exercises.models;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewExerciseDto(@NotBlank String name, String description, @NotEmpty List<@Positive Long> musclesWorked,
                             @NotNull @Positive Long primaryMuscleId, @NotNull ExerciseType exerciseType) {
}
