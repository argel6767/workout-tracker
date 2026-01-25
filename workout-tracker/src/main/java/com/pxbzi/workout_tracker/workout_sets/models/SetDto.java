package com.pxbzi.workout_tracker.workout_sets.models;

public record SetDto(Long id, Integer reps, Double weight) {
    public static SetDto getSetDto(WorkoutSet workoutSet) {
        return new SetDto(workoutSet.getId(), workoutSet.getReps(), workoutSet.getWeight());
    }
}
