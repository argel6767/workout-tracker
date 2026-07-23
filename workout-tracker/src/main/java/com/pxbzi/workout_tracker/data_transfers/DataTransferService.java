package com.pxbzi.workout_tracker.data_transfers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.data_transfers.models.DataExportDto;
import com.pxbzi.workout_tracker.data_transfers.models.ExerciseTransferDto;
import com.pxbzi.workout_tracker.data_transfers.models.MuscleTransferDto;
import com.pxbzi.workout_tracker.data_transfers.models.WorkoutTransferDto;
import com.pxbzi.workout_tracker.exercises.ExerciseRepository;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseMuscle;
import com.pxbzi.workout_tracker.muscles.MuscleRepository;
import com.pxbzi.workout_tracker.muscles.models.Muscle;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutRepository;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataTransferService {

    private final WorkoutRepository workoutRepository;
    private final MuscleRepository muscleRepository;
    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public DataExportDto exportData() {
        List<WorkoutTransferDto> workouts = workoutRepository.findAll().stream()
                .map(WorkoutTransferDto::of)
                .collect(Collectors.toList());

        List<ExerciseTransferDto> exercises = exerciseRepository.findAll().stream()
                .map(ExerciseTransferDto::of)
                .collect(Collectors.toList());

        List<MuscleTransferDto> muscles = muscleRepository.findAll().stream()
                .map(MuscleTransferDto::of)
                .collect(Collectors.toList());

        return new DataExportDto(workouts, exercises, muscles);
    }

    @Transactional(readOnly = true)
    public byte[] exportDataAsFile() throws IOException {
        DataExportDto data = exportData();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
    }

    @Transactional
    public void importData(DataExportDto data) {
        List<Muscle> savedMuscles = muscleRepository.saveAll(
                data.muscles().stream()
                        .map(dto -> new Muscle(dto.name(), dto.muscleGroup()))
                        .toList()
        );

        Map<String, Muscle> muscleByName = savedMuscles.stream()
                .collect(Collectors.toMap(Muscle::getName, m -> m));

        List<Exercise> savedExercises = exerciseRepository.saveAll(
                data.exercises().stream()
                        .map(dto -> {
                            Muscle primaryMuscle = muscleByName.get(dto.primaryMuscle());
                            if (primaryMuscle == null || !dto.musclesWorked().contains(dto.primaryMuscle())) {
                                throw new IllegalArgumentException(
                                        "Exercise '" + dto.name() + "' has an invalid or missing primary muscle"
                                );
                            }
                            Exercise exercise = Exercise.builder()
                                    .name(dto.name())
                                    .description(dto.description())
                                    .exerciseType(dto.exerciseType())
                                    .primaryMuscle(primaryMuscle)
                                    .build();
                            List<ExerciseMuscle> exerciseMuscles = dto.musclesWorked().stream()
                                    .map(muscleName -> new ExerciseMuscle(exercise, muscleByName.get(muscleName)))
                                    .toList();
                            exercise.setMusclesWorked(exerciseMuscles);
                            return exercise;
                        })
                        .toList()
        );

        Map<String, Exercise> exerciseByName = savedExercises.stream()
                .collect(Collectors.toMap(Exercise::getName, e -> e));

        workoutRepository.saveAll(
                data.workouts().stream()
                        .map(dto -> {
                            Workout workout = new Workout();
                            workout.setExercise(exerciseByName.get(dto.exercise()));
                            workout.setWorkoutDate(dto.workoutDate());
                            List<WorkoutSet> sets = dto.sets().stream()
                                    .map(setDto -> new WorkoutSet(workout, setDto.reps(), setDto.weight()))
                                    .toList();
                            workout.setWorkoutSets(sets);
                            return workout;
                        })
                        .toList()
        );
    }

    @Transactional
    public void importDataFromFile(MultipartFile file) throws IOException {
        DataExportDto data = objectMapper.readValue(file.getInputStream(), DataExportDto.class);
        importData(data);
    }
}
