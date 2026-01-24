package com.pxbzi.workout_tracker.muscles.models;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MuscleGroup {
    CHEST,
    BACK,
    SHOULDERS,
    ARMS,
    CORE,
    LEGS;

    @JsonCreator
    public static MuscleGroup fromValue(String value) {
        if (value == null) {
            return null;
        }
        return MuscleGroup.valueOf(value.toUpperCase());
    }
}
