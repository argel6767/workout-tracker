package com.pxbzi.workout_tracker.muscles.models;

public record MuscleDto(Long id, String name, MuscleGroup muscleGroup) {
    public static MuscleDto getMuscleDTO(Muscle muscle) {
        return new MuscleDto(muscle.getId(), muscle.getName(),  muscle.getMuscleGroup());
    }
}
