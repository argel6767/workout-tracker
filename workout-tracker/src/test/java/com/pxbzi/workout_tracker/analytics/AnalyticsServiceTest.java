package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.gemini.GeminiService;
import com.pxbzi.workout_tracker.muscles.MuscleService;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.workout_sets.WorkoutSetRepository;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class AnalyticsServiceTest {

    private AnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new AnalyticsService(
                mock(WorkoutService.class),
                mock(WorkoutSetRepository.class),
                mock(WeightService.class),
                mock(GeminiService.class),
                new ObjectMapper(),
                mock(MuscleService.class)
        );
    }

    @Test
    void weeklyVolumeRequiresExactlyOneTarget() {
        assertThatThrownBy(() -> service.getWeeklyVolumeAnalysis(
                null, null, LocalDate.now(), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("either muscleId or muscleGroup");

        assertThatThrownBy(() -> service.getWeeklyVolumeAnalysis(
                1L, MuscleGroup.CHEST, LocalDate.now(), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("either muscleId or muscleGroup");
    }

    @Test
    void weeklyVolumeRequiresPositiveLookback() {
        assertThatThrownBy(() -> service.getWeeklyVolumeAnalysis(
                null, MuscleGroup.CHEST, LocalDate.now(), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 1");
    }

    @Test
    void strongestOverviewRequiresAtLeastOneCategory() {
        assertThatThrownBy(() -> service.getAllStrongestExercises(false, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one");
    }
}
