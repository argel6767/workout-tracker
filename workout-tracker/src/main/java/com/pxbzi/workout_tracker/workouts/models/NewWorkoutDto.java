package com.pxbzi.workout_tracker.workouts.models;

import java.util.List;

public record NewWorkoutDto(Long exerciseId, List<NewSetDto> sets) {

}
