package com.pxbzi.workout_tracker.controllers;

import com.pxbzi.workout_tracker.analytics.AnalyticsController;
import com.pxbzi.workout_tracker.analytics.AnalyticsService;
import com.pxbzi.workout_tracker.data_transfers.DataTransferController;
import com.pxbzi.workout_tracker.data_transfers.DataTransferService;
import com.pxbzi.workout_tracker.data_transfers.models.DataExportDto;
import com.pxbzi.workout_tracker.exercises.ExerciseController;
import com.pxbzi.workout_tracker.exercises.ExerciseService;
import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.exercises.models.NewExerciseDto;
import com.pxbzi.workout_tracker.muscles.MuscleController;
import com.pxbzi.workout_tracker.muscles.MuscleService;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.muscles.models.NewMuscleDto;
import com.pxbzi.workout_tracker.weights.WeightController;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.weights.models.NewWeightDto;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import com.pxbzi.workout_tracker.workouts.WorkoutController;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerLevelTests {

    @Nested
    class MuscleControllerTests {

        @Test
        void createsMuscleAndReturnsLocation() {
            MuscleService service = mock(MuscleService.class);
            NewMuscleDto request = new NewMuscleDto("Chest", MuscleGroup.CHEST);
            MuscleDto response = new MuscleDto(11L, "Chest", MuscleGroup.CHEST);
            when(service.createMuscle(request)).thenReturn(response);
            setCurrentRequest("POST", "/v1/muscles");

            var result = new MuscleController(service).createMuscle(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getHeaders().getLocation()).hasPath("/v1/muscles/11");
            assertThat(result.getBody()).isEqualTo(response);
        }

        @Test
        void deletesMuscleAndReturnsNoContent() {
            MuscleService service = mock(MuscleService.class);

            var result = new MuscleController(service).deleteMuscle(11L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(service).deleteMuscleById(11L);
        }
    }

    @Nested
    class ExerciseControllerTests {

        @Test
        void createsExerciseAndReturnsLocation() {
            ExerciseService service = mock(ExerciseService.class);
            NewExerciseDto request = new NewExerciseDto("Bench Press", "Barbell press", List.of(1L), 1L, ExerciseType.FREE_WEIGHT);
            ExerciseDTO response = new ExerciseDTO(21L, "Bench Press", "Barbell press", List.of(), null, "CHEST", ExerciseType.FREE_WEIGHT);
            when(service.createExercise(request)).thenReturn(response);
            setCurrentRequest("POST", "/v1/exercises");

            var result = new ExerciseController(service).createExercise(request);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(result.getHeaders().getLocation()).hasPath("/v1/exercises/21");
            assertThat(result.getBody()).isEqualTo(response);
        }

        @Test
        void returnsAllExercises() {
            ExerciseService service = mock(ExerciseService.class);
            when(service.getAllExercises()).thenReturn(List.of());

            var result = new ExerciseController(service).getAllExercises();

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEmpty();
        }
    }

    @Nested
    class WorkoutControllerTests {

        @Test
        void returnsWorkoutsForRequestedDate() {
            WorkoutService service = mock(WorkoutService.class);
            LocalDate date = LocalDate.of(2026, 7, 20);
            when(service.getWorkoutsByDate(date)).thenReturn(List.of());

            var result = new WorkoutController(service).getWorkoutsByDate(date);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEmpty();
            verify(service).getWorkoutsByDate(date);
        }

        @Test
        void deletesWorkoutAndReturnsNoContent() {
            WorkoutService service = mock(WorkoutService.class);

            var result = new WorkoutController(service).deleteWorkoutById(31L);

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(service).deleteWorkout(31L);
        }
    }

    @Nested
    class WeightControllerTests {

        @Test
        void createsWeight() {
            WeightService service = mock(WeightService.class);
            NewWeightDto request = new NewWeightDto(180.5, LocalDate.of(2026, 7, 20));
            WeightDto response = new WeightDto(41L, 180.5, request.entryDate());
            when(service.createWeight(request)).thenReturn(response);

            WeightDto result = new WeightController(service).createWeight(request);

            assertThat(result).isEqualTo(response);
            verify(service).createWeight(request);
        }

        @Test
        void delegatesDateRangeQuery() {
            WeightService service = mock(WeightService.class);
            when(service.getAllWeightsInDateRange(3)).thenReturn(List.of());

            List<WeightDto> result = new WeightController(service).getWeightsByDate(3);

            assertThat(result).isEmpty();
            verify(service).getAllWeightsInDateRange(3);
        }
    }

    @Nested
    class AnalyticsControllerTests {

        @Test
        void delegatesExerciseProgressRequest() {
            AnalyticsService service = mock(AnalyticsService.class);

            new AnalyticsController(service).getWorkoutAnalyticsByExerciseId(51L, 6);

            verify(service).getWorkoutAnalyticsByExerciseId(51L, 6);
        }

        @Test
        void delegatesWeeklyVolumeRequest() {
            AnalyticsService service = mock(AnalyticsService.class);
            LocalDate date = LocalDate.of(2026, 7, 20);

            new AnalyticsController(service).getWeeklyVolumeAnalysis(null, MuscleGroup.CHEST, date, 5);

            verify(service).getWeeklyVolumeAnalysis(null, MuscleGroup.CHEST, date, 5);
        }
    }

    @Nested
    class DataTransferControllerTests {

        @Test
        void exportsAllData() {
            DataTransferService service = mock(DataTransferService.class);
            DataExportDto export = new DataExportDto(List.of(), List.of(), List.of());
            when(service.exportData()).thenReturn(export);

            DataExportDto result = new DataTransferController(service).exportData();

            assertThat(result).isSameAs(export);
            verify(service).exportData();
        }

        @Test
        void exportsDownloadWithJsonHeaders() throws Exception {
            DataTransferService service = mock(DataTransferService.class);
            when(service.exportDataAsFile()).thenReturn("{}".getBytes());

            var result = new DataTransferController(service).exportDataAsFile();

            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getHeaders().getContentDisposition().getFilename()).isEqualTo("workout-data.json");
            assertThat(result.getBody()).containsExactly("{}".getBytes());
        }
    }

    private static void setCurrentRequest(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
