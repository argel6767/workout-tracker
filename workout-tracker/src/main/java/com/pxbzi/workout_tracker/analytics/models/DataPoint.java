package com.pxbzi.workout_tracker.analytics.models;

import java.time.LocalDate;

public record DataPoint(LocalDate date, Double value) {
}
