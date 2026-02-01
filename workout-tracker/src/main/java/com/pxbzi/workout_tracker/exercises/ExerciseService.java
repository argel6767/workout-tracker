package com.pxbzi.workout_tracker.exercises;

import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.exercises.models.ExerciseMuscle;
import com.pxbzi.workout_tracker.exercises.models.NewExerciseDto;
import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleRepository muscleRepository;

    public ExerciseDTO createExercise(NewExerciseDto dto) {
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
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow();
        exercise.setName(dto.name());
        exercise.setDescription(dto.description());
        Exercise updatedExercise = exerciseRepository.save(exercise);
        return ExerciseDTO.getExerciseDTO(updatedExercise);
    }

    public void deleteExercise(Long id){
        exerciseRepository.deleteById(id);
    }

    private void mapMusclesWorked(Exercise exercise, NewExerciseDto dto) {
        List<Muscle> musclesWorked= muscleRepository.findAllById(dto.musclesWorked());
        List<ExerciseMuscle> exerciseMuscles = musclesWorked.stream().map(muscle -> new ExerciseMuscle(exercise, muscle)).toList();
        exercise.setName(dto.name());
        exercise.setDescription(dto.description());
        exercise.setMusclesWorked(exerciseMuscles);
    }


}
