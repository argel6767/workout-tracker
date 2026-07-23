package com.pxbzi.workout_tracker.integration;

import com.pxbzi.workout_tracker.exercises.ExerciseRepository;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import com.pxbzi.workout_tracker.weights.WeightRepository;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private MuscleRepository muscleRepository;

    @Autowired
    private WeightRepository weightRepository;

    @BeforeEach
    void resetDatabase() {
        workoutRepository.deleteAll();
        exerciseRepository.deleteAll();
        muscleRepository.deleteAll();
        weightRepository.deleteAll();
    }

    @Test
    void seededDataFlowsThroughPersistenceAndHttpEndpoints() throws Exception {
        String seedData = new ClassPathResource("seed/workout-data.json")
                .getContentAsString(StandardCharsets.UTF_8);
        LocalDate currentDate = LocalDate.now();

        mockMvc.perform(post("/v1/data-transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(seedData))
                .andExpect(status().isOk());

        mockMvc.perform(post("/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "weight": 180.5,
                                  "entryDate": "%s"
                                }
                                """.formatted(currentDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weight").value(180.5));

        mockMvc.perform(get("/v1/muscles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").isNotEmpty());

        mockMvc.perform(get("/v1/exercises"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bench Press"))
                .andExpect(jsonPath("$[0].musclesWorked.length()").value(2));

        mockMvc.perform(get("/v1/workouts/dates").param("date", "2026-07-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exercise.name").value("Bench Press"))
                .andExpect(jsonPath("$[0].sets.length()").value(2));

        mockMvc.perform(get("/v1/weights/dates").param("numMonthsBack", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weight").value(180.5))
                .andExpect(jsonPath("$[0].entryDate").value(currentDate.toString()));

        mockMvc.perform(get("/v1/analytics/progress/workouts-breakdown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("CHEST"))
                .andExpect(jsonPath("$[0].value").value(1));

        mockMvc.perform(get("/v1/data-transfers/file"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"workout-data.json\""));
    }

    @Test
    void missingResourceReturnsStructuredNotFoundResponse() throws Exception {
        mockMvc.perform(get("/v1/muscles/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.requestUri").value("/v1/muscles/999999"));
    }
}
