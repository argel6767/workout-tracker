package com.pxbzi.workout_tracker.workouts;

import com.pxbzi.workout_tracker.exercises.ExerciseService;
import com.pxbzi.workout_tracker.exercises.models.*;
import com.pxbzi.workout_tracker.muscles.models.*;
import com.pxbzi.workout_tracker.workout_sets.models.NewSetDto;
import com.pxbzi.workout_tracker.workouts.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkoutServiceTest {

    private WorkoutRepository repository;
    private ExerciseService exerciseService;
    private WorkoutService service;
    private Exercise exercise;

    @BeforeEach
    void setUp() {
        repository = mock(WorkoutRepository.class);
        exerciseService = mock(ExerciseService.class);
        service = new WorkoutService(repository, exerciseService);
        Muscle chest = new Muscle("Chest", MuscleGroup.CHEST);
        chest.setId(1L);
        exercise = Exercise.builder().id(2L).name("Bench press").description("")
                .exerciseType(ExerciseType.FREE_WEIGHT).primaryMuscle(chest)
                .musclesWorked(new ArrayList<>()).build();
        exercise.getMusclesWorked().add(new ExerciseMuscle(exercise, chest));
    }

    @Test
    void createsWorkoutWithSets() {
        LocalDate date = LocalDate.of(2026, 7, 22);
        when(exerciseService.getExerciseEntity(2L)).thenReturn(exercise);
        when(repository.save(any())).thenAnswer(call -> {
            Workout workout = call.getArgument(0);
            workout.setId(7L);
            return workout;
        });

        WorkoutDto result = service.createWorkout(
                new NewWorkoutDto(2L, List.of(new NewSetDto(8, 185.0)), date)
        );

        assertThat(result.id()).isEqualTo(7L);
        assertThat(result.exercise().id()).isEqualTo(2L);
        assertThat(result.sets()).singleElement().satisfies(set -> {
            assertThat(set.reps()).isEqualTo(8);
            assertThat(set.weight()).isEqualTo(185.0);
        });
    }

    @Test
    void rejectsMissingRequestBody() {
        assertThatThrownBy(() -> service.createWorkout(null))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo("required request body is null"));
    }

    @Test
    void rejectsMissingExerciseId() {
        NewWorkoutDto request = new NewWorkoutDto(
                null, List.of(new NewSetDto(8, 185.0)), LocalDate.now()
        );

        assertThatThrownBy(() -> service.createWorkout(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo("required exerciseId is null"));
    }

    @Test
    void rejectsEmptySets() {
        when(exerciseService.getExerciseEntity(2L)).thenReturn(exercise);

        assertThatThrownBy(() -> service.createWorkout(
                new NewWorkoutDto(2L, List.of(), LocalDate.now())))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo("No sets provided"));
    }
}
