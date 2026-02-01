package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.analytics.models.*;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.gemini.GeminiService;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import com.pxbzi.workout_tracker.gemini.models.ExerciseProgressionDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import com.pxbzi.workout_tracker.workout_sets.WorkoutSetRepository;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class AnalyticsService {

    private final WorkoutService workoutService;
    private final WorkoutSetRepository workoutSetRepository;
    private final WeightService weightService;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private static final int AGE = 23;
    private static final String SEX = "male";

    public AnalyticsDto getWorkoutAnalyticsByExerciseId(
        Long exerciseId,
        Integer numOfMonthsBack
    ) {
        List<Workout> workouts = workoutService.getWorkoutsByExerciseId(
            exerciseId,
            numOfMonthsBack
        );

        List<DataPoint> oneRepMaxes = workouts
            .stream()
            .map(workout ->
                new DataPoint(
                    workout.getWorkoutDate(),
                    calculateEstimatedOneRepMax(getTopSet(workout))
                )
            )
            .toList();

        List<DataPoint> avgWeightPerReps = workouts
            .stream()
            .map(workout -> {
                List<WorkoutSet> workoutSets = workout.getWorkoutSets();
                double avgWeightPerRep = calculateAvgWeightPerRep(workoutSets);
                return new DataPoint(workout.getWorkoutDate(), avgWeightPerRep);
            })
            .toList();

        List<DataPoint> totalVolumes = workouts
            .stream()
            .map(this::calculateTotalVolume)
            .toList();

        return new AnalyticsDto(oneRepMaxes, avgWeightPerReps, totalVolumes);
    }

    public StrongestExerciseByMuscleGroupDto getStrongestExerciseByMuscleGroup(
        MuscleGroup muscleGroup
    ) {
        WorkoutSet maxSet = workoutSetRepository
            .findSetsByMuscleGroup(muscleGroup)
            .stream()
            .max(Comparator.comparingDouble(WorkoutSet::getWeight))
            .orElseThrow();
        double oneRepMax = calculateEstimatedOneRepMax(maxSet);

        Exercise exercise = maxSet.getWorkout().getExercise();
        return new StrongestExerciseByMuscleGroupDto(
            exercise.getId(),
            exercise.getName(),
            oneRepMax,
            muscleGroup
        );
    }

    public StrongestExerciseByMuscleDto getStrongestExerciseByMuscle(
        Long muscleId
    ) {
        List<WorkoutSet> workoutSets = workoutSetRepository.findSetsByMuscleId(
            muscleId
        );

        Map<Exercise, List<WorkoutSet>> setsByExercise = workoutSets
            .stream()
            .collect(
                Collectors.groupingBy(set -> set.getWorkout().getExercise())
            );

        return setsByExercise
            .entrySet()
            .stream()
            .map(entry -> {
                Exercise exercise = entry.getKey();
                List<WorkoutSet> exerciseSets = entry.getValue();

                double maxE1RM = exerciseSets
                    .stream()
                    .mapToDouble(this::calculateEstimatedOneRepMax)
                    .max()
                    .orElse(0);

                double avgWeightPerRep = calculateAvgWeightPerRep(exerciseSets);

                return new StrongestExerciseByMuscleDto(
                    exercise.getId(),
                    exercise.getName(),
                    maxE1RM,
                    avgWeightPerRep
                );
            })
            .max(
                Comparator.comparingDouble(
                    StrongestExerciseByMuscleDto::oneRepMax
                )
            )
            .orElseThrow();
    }

    public List<RelativeStrengthDto> getRelativeStrength(
        int numMonthsBack,
        Long exerciseId
    ) {
        List<WeightDto> weights = weightService.getAllWeightsInDateRange(
            numMonthsBack
        );
        List<Workout> workouts = workoutService.getWorkoutsByExerciseId(
            exerciseId,
            numMonthsBack
        );

        // Create navigable maps for weights and workout sets by date
        NavigableMap<LocalDate, WeightDto> weightMap = new TreeMap<>();
        for (WeightDto weight : weights) {
            weightMap.put(weight.entryDate(), weight);
        }

        NavigableMap<LocalDate, WorkoutSet> setMap = new TreeMap<>();
        for (Workout workout : workouts) {
            WorkoutSet topSet = getTopSet(workout);
            setMap.put(workout.getWorkoutDate(), topSet);
        }

        // Collect all unique dates from both weights and workouts
        Set<LocalDate> allDates = new java.util.TreeSet<>();
        allDates.addAll(weightMap.keySet());
        allDates.addAll(setMap.keySet());

        return allDates
            .stream()
            .sorted()
            .map(date -> {
                // Get the workout set for this date, or the most recent one before it
                WorkoutSet workoutSet = setMap.get(date);
                if (workoutSet == null) {
                    Map.Entry<LocalDate, WorkoutSet> floorEntry =
                        setMap.floorEntry(date);
                    if (floorEntry != null) {
                        workoutSet = floorEntry.getValue();
                    }
                }

                // Get the weight for this date, or the most recent one before it
                WeightDto weightDto = weightMap.get(date);
                if (weightDto == null) {
                    Map.Entry<LocalDate, WeightDto> floorEntry =
                        weightMap.floorEntry(date);
                    if (floorEntry != null) {
                        weightDto = floorEntry.getValue();
                    }
                }

                // Skip if we don't have any prior data for either
                if (workoutSet == null || weightDto == null) {
                    return null;
                }

                double oneRepMax = calculateEstimatedOneRepMax(workoutSet);
                double weight = weightDto.weight();
                double relativeStrength = (oneRepMax / weight) * 100;
                return new RelativeStrengthDto(
                    weight,
                    oneRepMax,
                    relativeStrength,
                    date
                );
            })
            .filter(dto -> dto != null)
            .toList();
    }

    public ChatResponseDto analyzeExerciseProgression(Long exerciseId)
        throws JsonProcessingException {
        WorkoutDto workout = workoutService.getNewestWorkoutByExercise(
            exerciseId
        );
        WeightDto weight = weightService.getNewestWeightEntry();
        SetDto topSet = workout
            .sets()
            .stream()
            .max(Comparator.comparingDouble(SetDto::weight))
            .orElseThrow();
        ExerciseProgressionDto dto =
            ExerciseProgressionDto.getExerciseProgressionDto(
                workout,
                topSet,
                weight,
                AGE,
                SEX
            );

        String dtoStringfy = objectMapper.writeValueAsString(dto);
        return geminiService.getChatResponseDto(dtoStringfy);
    }

    private WorkoutSet getTopSet(Workout workout) {
        return workout
            .getWorkoutSets()
            .stream()
            .max(Comparator.comparingDouble(WorkoutSet::getWeight))
            .orElseThrow();
    }

    private DataPoint calculateTotalVolume(Workout workout) {
        List<WorkoutSet> workoutSets = workout.getWorkoutSets();
        double volume = 0;

        for (WorkoutSet workoutSet : workoutSets) {
            double weight = workoutSet.getWeight().equals(0.0)
                ? 1
                : workoutSet.getWeight();
            volume += (workoutSet.getReps() * weight);
        }
        return new DataPoint(workout.getWorkoutDate(), volume);
    }

    private double calculateAvgWeightPerRep(List<WorkoutSet> workoutSets) {
        double volume = 0;
        int reps = 0;

        for (WorkoutSet workoutSet : workoutSets) {
            double weight = workoutSet.getWeight().equals(0.0)
                ? 1
                : workoutSet.getWeight();
            volume += (workoutSet.getReps() * weight);
            reps += workoutSet.getReps();
        }

        return volume / reps;
    }

    private double calculateEstimatedOneRepMax(WorkoutSet maxWorkoutSet) {
        double weight = maxWorkoutSet.getWeight().equals(0.0)
            ? 1
            : maxWorkoutSet.getWeight();
        return weight * (1 + ((double) maxWorkoutSet.getReps() / 30));
    }

    private Pair<LocalDate, LocalDate> calculateDateRange(int numMonthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(numMonthsBack);
        return Pair.of(startDate, endDate);
    }
}
