package com.pxbzi.workout_tracker.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiContractIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void publishesEveryApiDomainAndValidationSchema() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value("3.1.0"))
                .andExpect(jsonPath("$.paths['/v1/muscles']").exists())
                .andExpect(jsonPath("$.paths['/v1/exercises']").exists())
                .andExpect(jsonPath("$.paths['/v1/workouts']").exists())
                .andExpect(jsonPath("$.paths['/v1/weights']").exists())
                .andExpect(jsonPath("$.paths['/v1/analytics/progress/exercise']").exists())
                .andExpect(jsonPath("$.paths['/v1/analytics/progress/normalized-strength']").exists())
                .andExpect(jsonPath("$.paths['/v1/data-transfers']").exists())
                .andExpect(jsonPath("$.paths['/v1/gemini/query']").exists())
                .andExpect(jsonPath("$.components.schemas.NewMuscleDto.required").isArray())
                .andExpect(jsonPath("$.components.schemas.NewWorkoutDto.required").isArray());
    }
}
