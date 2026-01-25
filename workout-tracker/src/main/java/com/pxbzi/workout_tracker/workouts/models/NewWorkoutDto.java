package com.pxbzi.workout_tracker.workouts.models;

import com.pxbzi.workout_tracker.workout_sets.models.NewSetDto;

import java.util.List;

public record NewWorkoutDto(Long exerciseId, List<NewSetDto> sets) {

}
