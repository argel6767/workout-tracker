package com.pxbzi.workout_tracker.analytics.calculations;

import com.pxbzi.workout_tracker.exercises.models.ExerciseType;

public final class StrengthCalculations {

    private StrengthCalculations() {}

    public static double effectiveWeight(
            double addedWeight,
            ExerciseType exerciseType,
            double bodyWeight
    ) {
        return exerciseType == ExerciseType.BODYWEIGHT
                ? bodyWeight + addedWeight
                : addedWeight;
    }

    public static double setVolume(
            int reps,
            double addedWeight,
            ExerciseType exerciseType,
            double bodyWeight
    ) {
        return reps * effectiveWeight(addedWeight, exerciseType, bodyWeight);
    }

    public static double averageWeightPerRep(double totalVolume, int totalReps) {
        return totalVolume / totalReps;
    }

    public static double estimatedOneRepMax(
            int reps,
            double addedWeight,
            ExerciseType exerciseType,
            double bodyWeight
    ) {
        double weight = effectiveWeight(addedWeight, exerciseType, bodyWeight);
        return weight * (1 + ((double) reps / 30));
    }
}
