package com.pxbzi.workout_tracker.data_transfers.models;

import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;

public record SetTransferDto(int reps, double weight) {
    public static SetTransferDto of(WorkoutSet set) {
        return new SetTransferDto(set.getReps(), set.getWeight());
    }
}