package com.pxbzi.workout_tracker.exercises;

import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.exercises.models.ExerciseMuscle;
import com.pxbzi.workout_tracker.exercises.models.NewExerciseDto;
import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleRepository muscleRepository;

    public ExerciseDTO createExercise(NewExerciseDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required request body is null");
        }
        Exercise exercise = new Exercise();
        mapMusclesWorked(exercise, dto);
        Exercise newExercise = exerciseRepository.save(exercise);
        return ExerciseDTO.getExerciseDTO(newExercise);
    }

    public List<ExerciseDTO> bulkCreateExercises(List<NewExerciseDto> dtos) {
        List<Exercise> exercises = new ArrayList<>();
        dtos.forEach(dto -> {
            Exercise exercise = new Exercise();
            mapMusclesWorked(exercise, dto);
            exercises.add(exercise);
        });

        List<Exercise> newExercises = exerciseRepository.saveAll(exercises);
        return newExercises.stream()
                .map(ExerciseDTO::getExerciseDTO)
                .toList();
    }

    public Exercise getExerciseEntity(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow();
    }

    public ExerciseDTO getExercise(Long id) {
        Exercise exercise = getExerciseEntity(id);
        return ExerciseDTO.getExerciseDTO(exercise);
    }

    public List<ExerciseDTO> getAllExercises() {
        List<Exercise> exercises= exerciseRepository.findAll();
        return exercises.stream()
                .map(ExerciseDTO::getExerciseDTO)
                .toList();
    }

    public ExerciseDTO updateExercise(Long id, ExerciseDTO dto) {
        if (!id.equals(dto.id())) {
            throw new IllegalArgumentException("Exercise ID mismatch");
        }
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow();
        exercise.setName(dto.name());
        exercise.setDescription(dto.description());
        exercise.setExerciseType(dto.exerciseType());
        mapMusclesWorked(exercise, dto);
        Exercise updatedExercise = exerciseRepository.save(exercise);
        return ExerciseDTO.getExerciseDTO(updatedExercise);
    }

    public void deleteExercise(Long id){
        exerciseRepository.deleteById(id);
    }

    private void mapMusclesWorked(Exercise exercise, NewExerciseDto dto) {
        if (dto.musclesWorked() == null || dto.musclesWorked().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required muscle id's are missing");
        }
        List<Muscle> musclesWorked= muscleRepository.findAllById(dto.musclesWorked());
        List<ExerciseMuscle> exerciseMuscles = musclesWorked.stream().map(muscle -> new ExerciseMuscle(exercise, muscle)).toList();
        if (dto.name() == null || dto.name().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required name is missing");
        }
        exercise.setName(dto.name());
        exercise.setDescription(dto.description() == null ? "" : dto.description());
        exercise.setMusclesWorked(exerciseMuscles);
        if (dto.exerciseType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required exercise type is missing");
        }
        exercise.setExerciseType(dto.exerciseType());
    }
    
    private void mapMusclesWorked(Exercise exercise, ExerciseDTO dto) {
        List<ExerciseMuscle> exerciseMuscles = exercise.getMusclesWorked();
        
        Map<Long, ExerciseMuscle> muscleMap = exerciseMuscles.stream()
            .collect(Collectors.toMap(exerciseMuscle -> exerciseMuscle.getMuscle().getId(), Function.identity()));
        
        for (MuscleDto muscleDto: dto.musclesWorked()) {
            if (!muscleMap.containsKey(muscleDto.id())) {
                Muscle muscle = muscleRepository.findById(muscleDto.id()).orElseThrow();
                ExerciseMuscle exerciseMuscle = new ExerciseMuscle(exercise, muscle);
                exerciseMuscles.add(exerciseMuscle);
            }
        }
        
    }


}
