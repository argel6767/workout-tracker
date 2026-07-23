package com.pxbzi.workout_tracker.workouts.models;

import com.pxbzi.workout_tracker.workout_sets.models.NewSetDto;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewWorkoutDto(
    @NotNull @Positive Long exerciseId,
    @NotEmpty List<@Valid NewSetDto> sets,
    LocalDate workoutDate
) {}
