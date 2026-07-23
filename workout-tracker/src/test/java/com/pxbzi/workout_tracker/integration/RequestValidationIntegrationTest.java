package com.pxbzi.workout_tracker.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RequestValidationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rejectsMissingAndBlankMuscleFields() throws Exception {
        mockMvc.perform(post("/v1/muscles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \",\"muscleGroup\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInvalidExerciseReferencesAndEmptyMuscles() throws Exception {
        mockMvc.perform(post("/v1/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Curl","description":"","musclesWorked":[],"primaryMuscleId":0,"exerciseType":null}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsEmptyWorkoutAndInvalidSetValues() throws Exception {
        mockMvc.perform(post("/v1/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseId\":1,\"sets\":[]}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/v1/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseId\":1,\"sets\":[{\"reps\":0,\"weight\":-1}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNonPositiveWeightAndBlankGeminiQuery() throws Exception {
        mockMvc.perform(post("/v1/weights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weight\":0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/v1/gemini/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMalformedJsonAndUnknownEnums() throws Exception {
        mockMvc.perform(post("/v1/muscles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/v1/muscles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Chest\",\"muscleGroup\":\"NOT_A_GROUP\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void allowsConfiguredCorsOriginAndRejectsUnknownOrigin() throws Exception {
        mockMvc.perform(options("/v1/muscles")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));

        mockMvc.perform(options("/v1/muscles")
                        .header("Origin", "https://unknown.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
