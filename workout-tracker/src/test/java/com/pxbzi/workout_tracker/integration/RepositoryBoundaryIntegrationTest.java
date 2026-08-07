package com.pxbzi.workout_tracker.integration;

import com.pxbzi.workout_tracker.exercises.ExerciseRepository;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.weights.WeightRepository;
import com.pxbzi.workout_tracker.weights.models.Weight;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryBoundaryIntegrationTest {

    @Autowired WorkoutRepository workouts;
    @Autowired WeightRepository weights;
    @Autowired MuscleRepository muscles;
    @Autowired ExerciseRepository exercises;

    private Exercise exercise;

    @BeforeEach
    void seedExercise() {
        Muscle chest = muscles.save(new Muscle("Boundary Chest", MuscleGroup.CHEST));
        exercise = exercises.save(Exercise.builder()
                .name("Boundary Press")
                .description("Boundary fixture")
                .exerciseType(ExerciseType.FREE_WEIGHT)
                .primaryMuscle(chest)
                .musclesWorked(new ArrayList<>())
                .build());
    }

    @Test
    void dateRangesIncludeBothBoundariesAndSortAscending() {
        LocalDate start = LocalDate.of(2024, 2, 29);
        LocalDate end = LocalDate.of(2024, 3, 31);
        saveWorkout(start, 100);
        saveWorkout(end, 120);
        saveWorkout(end.plusDays(1), 140);

        assertThat(workouts.findByExerciseAndDateRange(exercise.getId(), start, end))
                .extracting(Workout::getWorkoutDate)
                .containsExactly(start, end);
        assertThat(workouts.findNewestByExerciseId(exercise.getId()).orElseThrow().getWorkoutDate())
                .isEqualTo(end.plusDays(1));
    }

    @Test
    void weightRangesIncludeBoundariesAndNewestUsesLatestDate() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        saveWeight(start, 180);
        saveWeight(end, 178);
        saveWeight(end.plusDays(1), 177);

        assertThat(weights.findByEntryDateBetweenOrderByEntryDateAsc(start, end))
                .extracting(Weight::getEntryDate)
                .containsExactly(start, end);
        assertThat(weights.findNewest().orElseThrow().getEntryDate()).isEqualTo(end.plusDays(1));
    }

    @Test
    void muscleHistoryUsesPrimaryMuscleAndIncludesAllSessionsThroughEndDate() {
        Muscle biceps = muscles.save(new Muscle("Boundary Biceps", MuscleGroup.ARMS));
        Muscle triceps = muscles.save(new Muscle("Boundary Triceps", MuscleGroup.ARMS));
        Exercise curl = saveExercise("Boundary Curl", biceps);
        Exercise extension = saveExercise("Boundary Extension", triceps);
        saveWorkout(curl, LocalDate.of(2026, 1, 1), 40);
        saveWorkout(curl, LocalDate.of(2026, 2, 1), 50);
        saveWorkout(curl, LocalDate.of(2026, 3, 1), 60);
        saveWorkout(extension, LocalDate.of(2026, 2, 1), 70);

        assertThat(workouts.findByMuscleThroughDate(biceps.getId(), LocalDate.of(2026, 2, 1)))
                .extracting(Workout::getWorkoutDate)
                .containsExactly(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1));
        assertThat(workouts.findByMuscleGroupThroughDate(
                MuscleGroup.ARMS, LocalDate.of(2026, 2, 1)))
                .extracting(workout -> workout.getExercise().getName())
                .containsExactly("Boundary Curl", "Boundary Curl", "Boundary Extension");
    }

    private void saveWorkout(LocalDate date, double weight) {
        saveWorkout(exercise, date, weight);
    }

    private void saveWorkout(Exercise workoutExercise, LocalDate date, double weight) {
        Workout workout = new Workout();
        workout.setExercise(workoutExercise);
        workout.setWorkoutDate(date);
        workout.setWorkoutSets(List.of(new WorkoutSet(workout, 8, weight)));
        workouts.saveAndFlush(workout);
    }

    private Exercise saveExercise(String name, Muscle primaryMuscle) {
        return exercises.save(Exercise.builder()
                .name(name)
                .description("Boundary fixture")
                .exerciseType(ExerciseType.FREE_WEIGHT)
                .primaryMuscle(primaryMuscle)
                .musclesWorked(new ArrayList<>())
                .build());
    }

    private void saveWeight(LocalDate date, double value) {
        Weight weight = new Weight();
        weight.setEntryDate(date);
        weight.setWeight(value);
        weights.saveAndFlush(weight);
    }
}
