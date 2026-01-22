package com.pxbzi.workout_tracker.workouts.models;

public record SetDto(Long id, Integer reps, Double weight) {
    public static SetDto getSetDto(Set set) {
        return new SetDto(set.getId(), set.getReps(), set.getWeight());
    }
}
