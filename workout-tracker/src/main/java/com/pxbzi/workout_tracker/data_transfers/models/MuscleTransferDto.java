package com.pxbzi.workout_tracker.data_transfers.models;

import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;

public record MuscleTransferDto(String name, MuscleGroup muscleGroup) {
    public static MuscleTransferDto of(Muscle muscle) {
        return new MuscleTransferDto(muscle.getName(), muscle.getMuscleGroup());
    }
}