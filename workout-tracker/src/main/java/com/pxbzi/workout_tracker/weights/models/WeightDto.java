package com.pxbzi.workout_tracker.weights.models;

import java.time.LocalDate;

public record WeightDto(Long id, Double weight, LocalDate entryDate) {
    public static WeightDto getWeightDto(Weight weight) {
        return new WeightDto(weight.getId(), weight.getWeight(), weight.getEntryDate());
    }
}
