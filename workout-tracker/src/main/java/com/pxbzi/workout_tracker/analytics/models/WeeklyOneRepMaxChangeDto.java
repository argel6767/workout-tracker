package com.pxbzi.workout_tracker.analytics.models;

public record WeeklyOneRepMaxChangeDto(
        WeeklyOneRepMaxDto currentWeek,
        WeeklyOneRepMaxDto previousWeek,
        Double oneRepMaxChange,
        Double percentageChange
) {
}
