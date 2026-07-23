package com.pxbzi.workout_tracker.weights;

import com.pxbzi.workout_tracker.weights.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WeightServiceTest {

    private WeightRepository repository;
    private WeightService service;

    @BeforeEach
    void setUp() {
        repository = mock(WeightRepository.class);
        service = new WeightService(repository);
    }

    @Test
    void createsWeight() {
        LocalDate date = LocalDate.of(2026, 7, 22);
        when(repository.save(any())).thenAnswer(call -> {
            Weight weight = call.getArgument(0);
            weight.setId(4L);
            return weight;
        });

        WeightDto result = service.createWeight(new NewWeightDto(180.5, date));

        assertThat(result).isEqualTo(new WeightDto(4L, 180.5, date));
    }

    @Test
    void rejectsMissingRequestBody() {
        assertThatThrownBy(() -> service.createWeight(null))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void rejectsMissingWeightValue() {
        assertThatThrownBy(() -> service.createWeight(new NewWeightDto(null, LocalDate.now())))
                .isInstanceOfSatisfying(ResponseStatusException.class, exception ->
                        assertThat(exception.getReason()).isEqualTo("Required weight is missing"));
    }

    @Test
    void updatesWeight() {
        Weight weight = new Weight();
        weight.setId(4L);
        weight.setWeight(180.5);
        weight.setEntryDate(LocalDate.of(2026, 7, 1));
        when(repository.findById(4L)).thenReturn(Optional.of(weight));
        when(repository.save(weight)).thenReturn(weight);

        WeightDto result = service.updateWeight(
                4L,
                new WeightDto(4L, 179.0, LocalDate.of(2026, 7, 22))
        );

        assertThat(result.weight()).isEqualTo(179.0);
        assertThat(result.entryDate()).isEqualTo(LocalDate.of(2026, 7, 22));
    }
}
