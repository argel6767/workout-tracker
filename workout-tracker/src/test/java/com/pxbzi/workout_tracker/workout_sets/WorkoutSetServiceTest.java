package com.pxbzi.workout_tracker.workout_sets;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WorkoutSetServiceTest {

    private WorkoutSetRepository repository;
    private WorkoutSetService service;

    @BeforeEach
    void setUp() {
        repository = mock(WorkoutSetRepository.class);
        service = new WorkoutSetService();
        service.setWorkoutSetRepository(repository);
    }

    @Test
    void getsSetsByMuscleGroup() {
        List<WorkoutSet> sets = List.of(new WorkoutSet());
        when(repository.findSetsByMuscleGroup(MuscleGroup.CHEST)).thenReturn(sets);

        assertThat(service.getSetsByMuscleGroup(MuscleGroup.CHEST)).isSameAs(sets);
    }

    @Test
    void getsSetsByMuscleId() {
        List<WorkoutSet> sets = List.of(new WorkoutSet());
        when(repository.findSetsByMuscleId(1L)).thenReturn(sets);

        assertThat(service.getSetsByMuscleId(1L)).isSameAs(sets);
    }
}
