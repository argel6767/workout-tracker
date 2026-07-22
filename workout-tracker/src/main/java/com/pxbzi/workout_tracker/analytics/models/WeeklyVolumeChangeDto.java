package com.pxbzi.workout_tracker.analytics.models;

public record WeeklyVolumeChangeDto(
        WeeklyVolumeDto currentWeek,
        WeeklyVolumeDto previousWeek,
        double volumeChange,
        Double percentageChange
) {
}
