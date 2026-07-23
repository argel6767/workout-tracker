package com.pxbzi.workout_tracker.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.data_transfers.DataTransferService;
import com.pxbzi.workout_tracker.data_transfers.models.DataExportDto;
import com.pxbzi.workout_tracker.data_transfers.models.ExerciseTransferDto;
import com.pxbzi.workout_tracker.data_transfers.models.MuscleTransferDto;
import com.pxbzi.workout_tracker.exercises.ExerciseRepository;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class DataTransferTransactionIntegrationTest {

    @Autowired DataTransferService transfers;
    @Autowired ObjectMapper objectMapper;
    @Autowired WorkoutRepository workouts;
    @Autowired ExerciseRepository exercises;
    @Autowired MuscleRepository muscles;

    @BeforeEach
    void clearDatabase() {
        workouts.deleteAll();
        exercises.deleteAll();
        muscles.deleteAll();
    }

    @Test
    void exportCanBeImportedAsAnEquivalentRoundTrip() throws Exception {
        DataExportDto seed = objectMapper.readValue(
                new ClassPathResource("seed/workout-data.json").getInputStream(), DataExportDto.class);
        transfers.importData(seed);

        DataExportDto exported = transfers.exportData();
        workouts.deleteAll();
        exercises.deleteAll();
        muscles.deleteAll();
        transfers.importData(exported);

        DataExportDto restored = transfers.exportData();
        assertThat(restored.muscles()).containsExactlyInAnyOrderElementsOf(exported.muscles());
        assertThat(restored.exercises()).containsExactlyInAnyOrderElementsOf(exported.exercises());
        assertThat(restored.workouts()).containsExactlyInAnyOrderElementsOf(exported.workouts());
    }

    @Test
    void invalidImportRollsBackMusclesSavedBeforeTheFailure() {
        DataExportDto invalid = new DataExportDto(
                List.of(),
                List.of(new ExerciseTransferDto("Invalid Press", "", List.of("Chest"), "Missing", ExerciseType.FREE_WEIGHT)),
                List.of(new MuscleTransferDto("Chest", MuscleGroup.CHEST)));

        assertThatThrownBy(() -> transfers.importData(invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalid or missing primary muscle");
        assertThat(muscles.count()).isZero();
        assertThat(exercises.count()).isZero();
        assertThat(workouts.count()).isZero();
    }
}
