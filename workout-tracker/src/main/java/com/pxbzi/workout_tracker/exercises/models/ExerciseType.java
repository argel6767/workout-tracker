package com.pxbzi.workout_tracker.exercises.models;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum ExerciseType {
    BODYWEIGHT,
    MACHINE,
    CABLE,
    FREE_WEIGHT;
    
    @JsonCreator
    public static ExerciseType fromString(String value) {
        if (value == null) {
            return null;
        }
        return ExerciseType.valueOf(value.toUpperCase());
    }
}