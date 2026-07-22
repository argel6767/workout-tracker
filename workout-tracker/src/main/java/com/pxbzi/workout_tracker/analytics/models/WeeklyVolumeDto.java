package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import java.time.LocalDate;
import java.util.List;

public record WeeklyVolumeDto(LocalDate startDate, LocalDate endDate, List<WorkoutDto> workouts, double totalVolume) {
}
