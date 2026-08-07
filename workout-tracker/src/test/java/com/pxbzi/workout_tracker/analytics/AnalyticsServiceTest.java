package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.analytics.models.NormalizedStrengthAnalysisDto;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.gemini.GeminiService;
import com.pxbzi.workout_tracker.muscles.MuscleService;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.workout_sets.WorkoutSetRepository;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnalyticsServiceTest {

    private AnalyticsService service;
    private WorkoutService workoutService;
    private MuscleService muscleService;

    @BeforeEach
    void setUp() {
        workoutService = mock(WorkoutService.class);
        muscleService = mock(MuscleService.class);
        service = new AnalyticsService(
                workoutService,
                mock(WorkoutSetRepository.class),
                mock(WeightService.class),
                mock(GeminiService.class),
                new ObjectMapper(),
                muscleService
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

    @Test
    void normalizedStrengthUsesEachExercisesFirstSessionAndEqualWeeklyWeighting() {
        LocalDate reportDate = LocalDate.of(2026, 8, 5);
        Exercise curl = exercise(1L, "Curl");
        Exercise hammerCurl = exercise(2L, "Hammer curl");
        List<Workout> workouts = List.of(
                workout(1L, curl, LocalDate.of(2026, 7, 1), set(75, 10)),
                workout(2L, hammerCurl, LocalDate.of(2026, 7, 2), set(150, 10)),
                workout(3L, curl, LocalDate.of(2026, 7, 28), set(100, 1), set(90, 10)),
                workout(4L, curl, LocalDate.of(2026, 7, 30), set(75, 10)),
                workout(5L, hammerCurl, LocalDate.of(2026, 7, 31), set(180, 10)));
        when(muscleService.getMuscle(7L)).thenReturn(new MuscleDto(7L, "Biceps", MuscleGroup.ARMS));
        when(workoutService.getWorkoutsByMuscleThroughDate(7L, LocalDate.of(2026, 8, 9)))
                .thenReturn(workouts);

        NormalizedStrengthAnalysisDto analysis = service.getNormalizedStrengthAnalysis(
                7L, null, reportDate, 2);

        assertThat(analysis.targetName()).isEqualTo("Biceps");
        assertThat(analysis.trend()).singleElement().satisfies(point -> {
            assertThat(point.weekStart()).isEqualTo(LocalDate.of(2026, 7, 27));
            assertThat(point.averageStrengthIndex()).isEqualTo(115.0);
            assertThat(point.exerciseCount()).isEqualTo(2);
        });
    }

    @Test
    void normalizedStrengthRequiresPositiveLookback() {
        assertThatThrownBy(() -> service.getNormalizedStrengthAnalysis(
                7L, null, LocalDate.of(2026, 8, 5), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 1");
    }

    @Test
    void normalizedStrengthExcludesExerciseWithZeroFirstSessionBaseline() {
        LocalDate reportDate = LocalDate.of(2026, 8, 5);
        Exercise curl = exercise(1L, "Curl");
        when(muscleService.getMuscle(7L)).thenReturn(new MuscleDto(7L, "Biceps", MuscleGroup.ARMS));
        when(workoutService.getWorkoutsByMuscleThroughDate(7L, LocalDate.of(2026, 8, 9)))
                .thenReturn(List.of(
                        workout(1L, curl, LocalDate.of(2026, 7, 1), set(0, 10)),
                        workout(2L, curl, LocalDate.of(2026, 7, 30), set(75, 10))));

        NormalizedStrengthAnalysisDto analysis = service.getNormalizedStrengthAnalysis(
                7L, null, reportDate, 2);

        assertThat(analysis.trend()).isEmpty();
    }

    @Test
    void normalizedStrengthSupportsNonArmsMuscleGroups() {
        LocalDate reportDate = LocalDate.of(2026, 8, 5);
        Exercise row = exercise(3L, "Row");
        when(workoutService.getWorkoutsByMuscleGroupThroughDate(
                MuscleGroup.BACK, LocalDate.of(2026, 8, 9)))
                .thenReturn(List.of(
                        workout(1L, row, LocalDate.of(2026, 7, 1), set(75, 10)),
                        workout(2L, row, LocalDate.of(2026, 7, 30), set(90, 10))));

        NormalizedStrengthAnalysisDto analysis = service.getNormalizedStrengthAnalysis(
                null, MuscleGroup.BACK, reportDate, 2);

        assertThat(analysis.muscleId()).isNull();
        assertThat(analysis.muscleGroup()).isEqualTo(MuscleGroup.BACK);
        assertThat(analysis.targetName()).isEqualTo("BACK");
        assertThat(analysis.trend()).singleElement().satisfies(point ->
                assertThat(point.averageStrengthIndex()).isEqualTo(120.0));
    }

    @Test
    void normalizedStrengthRequiresExactlyOneTargetAndIndividualArmMuscle() {
        LocalDate date = LocalDate.of(2026, 8, 5);

        assertThatThrownBy(() -> service.getNormalizedStrengthAnalysis(null, null, date, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("either muscleId or muscleGroup");
        assertThatThrownBy(() -> service.getNormalizedStrengthAnalysis(
                7L, MuscleGroup.BACK, date, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("either muscleId or muscleGroup");
        assertThatThrownBy(() -> service.getNormalizedStrengthAnalysis(
                null, MuscleGroup.ARMS, date, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("muscleId");
    }

    private Exercise exercise(Long id, String name) {
        return Exercise.builder().id(id).name(name).exerciseType(ExerciseType.FREE_WEIGHT).build();
    }

    private Workout workout(Long id, Exercise exercise, LocalDate date, WorkoutSet... sets) {
        Workout workout = new Workout();
        workout.setId(id);
        workout.setExercise(exercise);
        workout.setWorkoutDate(date);
        workout.setWorkoutSets(List.of(sets));
        workout.getWorkoutSets().forEach(set -> set.setWorkout(workout));
        return workout;
    }

    private WorkoutSet set(double weight, int reps) {
        return new WorkoutSet(null, reps, weight);
    }
}
