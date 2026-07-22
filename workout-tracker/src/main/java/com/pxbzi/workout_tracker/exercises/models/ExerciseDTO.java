package com.pxbzi.workout_tracker.exercises.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;

import java.util.List;

public record ExerciseDTO(Long id, String name, String description, List<MuscleDto> musclesWorked,
                          MuscleDto primaryMuscle, String primaryMuscleGroup, ExerciseType exerciseType) {

    public static ExerciseDTO getExerciseDTO(Exercise exercise) {
        List<MuscleDto> musclesWorked = exercise.getMusclesWorked().stream()
                .map(exerciseMuscle -> MuscleDto.getMuscleDTO(exerciseMuscle.getMuscle()))
                .toList();

        MuscleDto primaryMuscle = MuscleDto.getMuscleDTO(exercise.getPrimaryMuscle());
        return new ExerciseDTO(exercise.getId(), exercise.getName(), exercise.getDescription(), musclesWorked,
                primaryMuscle, primaryMuscle.muscleGroup().name(), exercise.getExerciseType());
    }

    public static MuscleGroup findPrimaryMuscleGroup(Exercise exercise) {
        return exercise.getPrimaryMuscle().getMuscleGroup();
    }
}
