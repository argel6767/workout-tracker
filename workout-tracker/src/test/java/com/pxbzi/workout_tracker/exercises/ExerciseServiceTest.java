package com.pxbzi.workout_tracker.exercises;

import com.pxbzi.workout_tracker.exercises.models.*;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExerciseServiceTest {

    private ExerciseRepository exerciseRepository;
    private MuscleRepository muscleRepository;
    private ExerciseService service;
    private Muscle chest;

    @BeforeEach
    void setUp() {
        exerciseRepository = mock(ExerciseRepository.class);
        muscleRepository = mock(MuscleRepository.class);
        service = new ExerciseService(exerciseRepository, muscleRepository);
        chest = new Muscle("Chest", MuscleGroup.CHEST);
        chest.setId(1L);
    }

    @Test
    void createsExerciseAndDefaultsDescription() {
        when(muscleRepository.findAllById(List.of(1L))).thenReturn(List.of(chest));
        when(muscleRepository.findById(1L)).thenReturn(Optional.of(chest));
        when(exerciseRepository.save(any())).thenAnswer(call -> {
            Exercise exercise = call.getArgument(0);
            exercise.setId(2L);
            return exercise;
        });

        ExerciseDTO result = service.createExercise(
                new NewExerciseDto("Bench press", null, List.of(1L), 1L, ExerciseType.FREE_WEIGHT)
        );

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.description()).isEmpty();
        assertThat(result.musclesWorked()).hasSize(1);
        assertThat(result.primaryMuscle().id()).isEqualTo(1L);
    }

    @Test
    void rejectsMissingRequestBody() {
        assertThatThrownBy(() -> service.createExercise(null))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo("Required request body is null"));
    }

    @Test
    void rejectsEmptyMuscleList() {
        NewExerciseDto request = new NewExerciseDto(
                "Bench press", "", List.of(), 1L, ExerciseType.FREE_WEIGHT
        );

        assertThatThrownBy(() -> service.createExercise(request))
                .isInstanceOf(ResponseStatusException.class);
        verify(exerciseRepository, never()).save(any());
    }

    @Test
    void requiresPrimaryMuscleInMusclesWorked() {
        when(muscleRepository.findAllById(List.of(1L))).thenReturn(List.of(chest));
        NewExerciseDto request = new NewExerciseDto(
                "Bench press", "", List.of(1L), 3L, ExerciseType.FREE_WEIGHT
        );

        assertThatThrownBy(() -> service.createExercise(request))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getReason()).contains("Primary muscle"));
    }
}
