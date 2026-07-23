package com.pxbzi.workout_tracker.integration;

import com.pxbzi.workout_tracker.weights.WeightRepository;
import com.pxbzi.workout_tracker.weights.models.Weight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class PostgresCompatibilityTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("gemini.api.key", () -> "test-key");
    }

    @Autowired WeightRepository weights;

    @Test
    void schemaAndDateQueriesAreCompatibleWithPostgres() {
        Weight older = new Weight();
        older.setWeight(180.0);
        older.setEntryDate(LocalDate.of(2026, 1, 1));
        weights.save(older);

        Weight newer = new Weight();
        newer.setWeight(178.0);
        newer.setEntryDate(LocalDate.of(2026, 2, 1));
        weights.saveAndFlush(newer);

        assertThat(weights.findNewest().orElseThrow().getWeight()).isEqualTo(178.0);
        assertThat(weights.findByEntryDateBetweenOrderByEntryDateAsc(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 1)))
                .extracting(Weight::getWeight)
                .containsExactly(180.0, 178.0);
    }
}
