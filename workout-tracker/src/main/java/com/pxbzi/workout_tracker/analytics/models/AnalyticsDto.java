package com.pxbzi.workout_tracker.analytics.models;

import java.time.LocalDate;
import java.util.List;

public record AnalyticsDto(List<DataPoint<LocalDate, Double>> oneRepMaxes, List<DataPoint<LocalDate, Double>> avgWeightPerReps, List<DataPoint<LocalDate, Double>> totalVolumes) {
}
