package com.pxbzi.workout_tracker.analytics.calculations;

import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StrengthCalculationsTest {

    @Test
    void addsBodyWeightOnlyForBodyweightExercises() {
        assertThat(StrengthCalculations.effectiveWeight(
                25, ExerciseType.BODYWEIGHT, 175)).isEqualTo(200);
        assertThat(StrengthCalculations.effectiveWeight(
                25, ExerciseType.FREE_WEIGHT, 175)).isEqualTo(25);
    }

    @Test
    void calculatesSetVolumeAndEstimatedOneRepMax() {
        assertThat(StrengthCalculations.setVolume(
                10, 25, ExerciseType.BODYWEIGHT, 175)).isEqualTo(2_000);
        assertThat(StrengthCalculations.estimatedOneRepMax(
                10, 25, ExerciseType.BODYWEIGHT, 175)).isCloseTo(
                        266.67, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void calculatesAverageWeightPerRepFromVolumeAndReps() {
        assertThat(StrengthCalculations.averageWeightPerRep(2_000, 10)).isEqualTo(200);
    }
}
