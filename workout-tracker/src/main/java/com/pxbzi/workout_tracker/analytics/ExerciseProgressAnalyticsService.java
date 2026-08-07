package com.pxbzi.workout_tracker.analytics;

import com.pxbzi.workout_tracker.analytics.calculations.StrengthCalculations;
import com.pxbzi.workout_tracker.analytics.models.AnalyticsDto;
import com.pxbzi.workout_tracker.analytics.models.DataPoint;
import com.pxbzi.workout_tracker.analytics.models.RelativeStrengthDto;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.gemini.models.ExerciseProgressionDto;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class ExerciseProgressAnalyticsService {

    private static final int AGE = 24;
    private static final String SEX = "male";

    private final WorkoutService workoutService;
    private final WeightService weightService;

    public AnalyticsDto getWorkoutAnalyticsByExerciseId(Long exerciseId, Integer numOfMonthsBack) {
        List<Workout> workouts = workoutService.getWorkoutsByExerciseId(exerciseId, numOfMonthsBack);
        ExerciseType exerciseType = workouts.getLast().getExercise().getExerciseType();

        List<DataPoint<LocalDate, Double>> oneRepMaxes = workouts.stream()
                .map(workout -> new DataPoint<>(
                        workout.getWorkoutDate(),
                        calculateEstimatedOneRepMax(getTopSet(workout), exerciseType)))
                .toList();
        List<DataPoint<LocalDate, Double>> averageWeightsPerRep = workouts.stream()
                .map(workout -> new DataPoint<>(
                        workout.getWorkoutDate(),
                        calculateAverageWeightPerRep(workout.getWorkoutSets(), exerciseType)))
                .toList();
        List<DataPoint<LocalDate, Double>> totalVolumes = workouts.stream()
                .map(workout -> new DataPoint<>(
                        workout.getWorkoutDate(),
                        calculateTotalVolume(workout.getWorkoutSets(), exerciseType)))
                .toList();

        return new AnalyticsDto(oneRepMaxes, averageWeightsPerRep, totalVolumes);
    }

    public List<RelativeStrengthDto> getRelativeStrength(int numMonthsBack, Long exerciseId) {
        List<WeightDto> weights = weightService.getAllWeightsInDateRange(numMonthsBack);
        List<Workout> workouts = workoutService.getWorkoutsByExerciseId(exerciseId, numMonthsBack);
        ExerciseType exerciseType = workouts.getFirst().getExercise().getExerciseType();

        NavigableMap<LocalDate, WeightDto> weightMap = new TreeMap<>();
        weights.forEach(weight -> weightMap.put(weight.entryDate(), weight));
        NavigableMap<LocalDate, WorkoutSet> setMap = new TreeMap<>();
        workouts.forEach(workout -> setMap.put(workout.getWorkoutDate(), getTopSet(workout)));

        Set<LocalDate> allDates = new TreeSet<>();
        allDates.addAll(weightMap.keySet());
        allDates.addAll(setMap.keySet());

        return allDates.stream()
                .map(date -> toRelativeStrength(
                        date, floorValue(setMap, date), floorValue(weightMap, date), exerciseType))
                .filter(dto -> dto != null)
                .toList();
    }

    public ExerciseProgressionDto getExerciseProgression(Long exerciseId) {
        WorkoutDto workout = workoutService.getNewestWorkoutByExercise(exerciseId);
        WeightDto weight = weightService.getNewestWeightEntry();
        SetDto topSet = workout.sets().stream()
                .max(Comparator.comparingDouble(SetDto::weight))
                .orElseThrow();
        List<WorkoutDto> lastMonthsWorkouts = workoutService.getWorkoutsByExerciseId(exerciseId, 1)
                .stream()
                .map(WorkoutDto::getWorkoutDto)
                .toList();
        return ExerciseProgressionDto.getExerciseProgressionDto(
                workout, topSet, lastMonthsWorkouts, weight, AGE, SEX);
    }

    private RelativeStrengthDto toRelativeStrength(
            LocalDate date,
            WorkoutSet workoutSet,
            WeightDto weight,
            ExerciseType exerciseType
    ) {
        if (workoutSet == null || weight == null) {
            return null;
        }
        double oneRepMax = calculateEstimatedOneRepMax(workoutSet, exerciseType);
        return new RelativeStrengthDto(
                weight.weight(), oneRepMax, (oneRepMax / weight.weight()) * 100, date);
    }

    private <T> T floorValue(NavigableMap<LocalDate, T> values, LocalDate date) {
        Map.Entry<LocalDate, T> entry = values.floorEntry(date);
        return entry == null ? null : entry.getValue();
    }

    private WorkoutSet getTopSet(Workout workout) {
        return workout.getWorkoutSets().stream()
                .max(Comparator.comparingDouble(WorkoutSet::getWeight))
                .orElseThrow();
    }

    private double calculateTotalVolume(List<WorkoutSet> sets, ExerciseType exerciseType) {
        return sets.stream().mapToDouble(set -> StrengthCalculations.setVolume(
                set.getReps(), set.getWeight(), exerciseType, getBodyWeight(exerciseType))).sum();
    }

    private double calculateAverageWeightPerRep(List<WorkoutSet> sets, ExerciseType exerciseType) {
        double volume = calculateTotalVolume(sets, exerciseType);
        int reps = sets.stream().mapToInt(WorkoutSet::getReps).sum();
        return StrengthCalculations.averageWeightPerRep(volume, reps);
    }

    private double calculateEstimatedOneRepMax(WorkoutSet set, ExerciseType exerciseType) {
        return StrengthCalculations.estimatedOneRepMax(
                set.getReps(), set.getWeight(), exerciseType, getBodyWeight(exerciseType));
    }

    private double getBodyWeight(ExerciseType exerciseType) {
        return exerciseType == ExerciseType.BODYWEIGHT
                ? weightService.getNewestWeightEntry().weight()
                : 0;
    }
}
