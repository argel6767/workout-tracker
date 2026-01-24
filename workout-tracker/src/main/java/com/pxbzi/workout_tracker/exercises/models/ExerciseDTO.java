package com.pxbzi.workout_tracker.exercises.models;

import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ExerciseDTO(Long id, String name, String description, List<MuscleDto> musclesWorked, String primaryMuscleGroup) {
    public static ExerciseDTO getExerciseDTO(Exercise exercise) {
        List<MuscleDto> musclesWorked = exercise.getMusclesWorked().stream()
                .map(exerciseMuscle -> MuscleDto.getMuscleDTO(exerciseMuscle.getMuscle()))
                .toList();
        
        Map<MuscleGroup, Long> muscleGroupFreq = musclesWorked.stream()
                .collect(Collectors.groupingBy(MuscleDto::muscleGroup, Collectors.counting()));
        
        MuscleGroup primaryMuscleGroup = muscleGroupFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No muscles worked"));
        
        return new ExerciseDTO(exercise.getId(), exercise.getName(), exercise.getDescription(), musclesWorked, primaryMuscleGroup.name());
    }
}
