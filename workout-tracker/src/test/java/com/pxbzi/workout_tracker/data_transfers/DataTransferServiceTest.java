package com.pxbzi.workout_tracker.data_transfers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.data_transfers.models.DataExportDto;
import com.pxbzi.workout_tracker.exercises.ExerciseRepository;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DataTransferServiceTest {

    @Test
    void exportsEveryDomainCollection() {
        WorkoutRepository workouts = mock(WorkoutRepository.class);
        ExerciseRepository exercises = mock(ExerciseRepository.class);
        MuscleRepository muscles = mock(MuscleRepository.class);
        when(workouts.findAll()).thenReturn(List.of());
        when(exercises.findAll()).thenReturn(List.of());
        when(muscles.findAll()).thenReturn(List.of());
        DataTransferService service = new DataTransferService(
                workouts, muscles, exercises, new ObjectMapper()
        );

        DataExportDto result = service.exportData();

        assertThat(result.workouts()).isEmpty();
        assertThat(result.exercises()).isEmpty();
        assertThat(result.muscles()).isEmpty();
        verify(workouts).findAll();
        verify(exercises).findAll();
        verify(muscles).findAll();
    }
}
