package com.pxbzi.workout_tracker.analytics.models;

import java.util.List;

public record AnalyticsDto(List<DataPoint> oneRepMaxes, List<DataPoint> avgWeightPerReps, List<DataPoint> totalVolumes) {
}
