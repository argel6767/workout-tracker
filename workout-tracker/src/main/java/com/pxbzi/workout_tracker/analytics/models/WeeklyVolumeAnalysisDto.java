package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import java.util.List;

public record WeeklyVolumeAnalysisDto(
        Long muscleId,
        MuscleGroup muscleGroup,
        String targetName,
        int numWeeksBack,
        List<WeeklyVolumeChangeDto> weeklyChanges
) {
}
