package com.pxbzi.workout_tracker.data_transfers.models;

import java.util.List;

public record DataExportDto(List<WorkoutTransferDto> workouts, List<ExerciseTransferDto> exercises, List<MuscleTransferDto> muscles) {
} 