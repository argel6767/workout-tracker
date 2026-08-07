package com.pxbzi.workout_tracker.analytics.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import java.util.List;

public record NormalizedStrengthAnalysisDto(
        Long muscleId,
        MuscleGroup muscleGroup,
        String targetName,
        Integer numWeeksBack,
        List<NormalizedStrengthPointDto> trend
) {
}
