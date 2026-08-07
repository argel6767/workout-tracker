package com.pxbzi.workout_tracker.analytics;

import com.pxbzi.workout_tracker.analytics.calculations.StrengthCalculations;
import com.pxbzi.workout_tracker.analytics.models.DataPoint;
import com.pxbzi.workout_tracker.analytics.models.WeeklyVolumeAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyVolumeChangeDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyVolumeDto;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.muscles.MuscleService;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.workout_sets.WorkoutSetRepository;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolumeAnalyticsService {

    private final WorkoutService workoutService;
    private final WorkoutSetRepository workoutSetRepository;
    private final WeightService weightService;
    private final MuscleService muscleService;

    public List<DataPoint<String, Integer>> aggregateWorkoutsByMuscle() {
        return workoutService.getAllWorkouts().stream()
                .collect(Collectors.groupingBy(
                        workout -> workout.exercise().primaryMuscleGroup(), Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new DataPoint<>(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }

    public List<DataPoint<String, Integer>> aggregateSetsByMuscle() {
        return workoutSetRepository.findAll().stream()
                .collect(Collectors.groupingBy(workoutSet -> {
                    Exercise exercise = workoutSet.getWorkout().getExercise();
                    return ExerciseDTO.findPrimaryMuscleGroup(exercise).name();
                }, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new DataPoint<>(entry.getKey(), entry.getValue().intValue()))
                .toList();
    }

    public List<DataPoint<String, Double>> aggregateTotalVolumeByMonth() {
        return workoutService.getAllWorkouts().stream()
                .collect(Collectors.groupingBy(
                        workout -> YearMonth.from(workout.workoutDate()),
                        Collectors.summingDouble(workout -> calculateTotalVolumeFromDtos(
                                workout.sets(), workout.exercise().exerciseType()))))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new DataPoint<>(
                        entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                + " " + entry.getKey().getYear(),
                        entry.getValue()))
                .toList();
    }

    public WeeklyVolumeAnalysisDto getWeeklyVolumeAnalysis(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) {
        validateTarget(muscleId, muscleGroup);
        validateNumWeeksBack(numWeeksBack);
        String targetName = muscleId != null
                ? muscleService.getMuscle(muscleId).name()
                : muscleGroup.name();
        WeeklyDateRange dateRange = calculateWeeklyDateRange(date, numWeeksBack);
        List<Workout> workouts = muscleId != null
                ? workoutService.getWorkoutsByMuscleAndDateRange(
                        muscleId, dateRange.earliestStart(), dateRange.currentEnd())
                : workoutService.getWorkoutsByMuscleGroupAndDateRange(
                        muscleGroup, dateRange.earliestStart(), dateRange.currentEnd());

        List<WeeklyVolumeDto> weeks = new ArrayList<>();
        for (int weeksAgo = numWeeksBack; weeksAgo >= 0; weeksAgo--) {
            LocalDate weekStart = dateRange.currentStart().minusWeeks(weeksAgo);
            LocalDate weekEnd = weekStart.plusDays(6);
            List<Workout> weeklyWorkouts = workouts.stream()
                    .filter(workout -> !workout.getWorkoutDate().isBefore(weekStart)
                            && !workout.getWorkoutDate().isAfter(weekEnd))
                    .toList();
            weeks.add(toWeeklyVolume(weekStart, weekEnd, weeklyWorkouts));
        }

        List<WeeklyVolumeChangeDto> changes = new ArrayList<>();
        for (int index = 1; index < weeks.size(); index++) {
            WeeklyVolumeDto previous = weeks.get(index - 1);
            WeeklyVolumeDto current = weeks.get(index);
            double change = current.totalVolume() - previous.totalVolume();
            Double percentage = previous.totalVolume() == 0
                    ? null
                    : (change / previous.totalVolume()) * 100;
            changes.add(new WeeklyVolumeChangeDto(current, previous, change, percentage));
        }
        return new WeeklyVolumeAnalysisDto(
                muscleId, muscleGroup, targetName, numWeeksBack, changes);
    }

    private WeeklyVolumeDto toWeeklyVolume(
            LocalDate startDate,
            LocalDate endDate,
            List<Workout> workouts
    ) {
        double totalVolume = workouts.stream().mapToDouble(workout -> calculateTotalVolume(
                workout.getWorkoutSets(), workout.getExercise().getExerciseType())).sum();
        return new WeeklyVolumeDto(
                startDate,
                endDate,
                workouts.stream().map(WorkoutDto::getWorkoutDto).toList(),
                totalVolume);
    }

    private double calculateTotalVolume(List<WorkoutSet> sets, ExerciseType exerciseType) {
        return sets.stream().mapToDouble(set -> StrengthCalculations.setVolume(
                set.getReps(),
                set.getWeight(),
                exerciseType,
                getBodyWeight(exerciseType))).sum();
    }

    private double calculateTotalVolumeFromDtos(List<SetDto> sets, ExerciseType exerciseType) {
        return sets.stream().mapToDouble(set -> StrengthCalculations.setVolume(
                set.reps(), set.weight(), exerciseType, getBodyWeight(exerciseType))).sum();
    }

    private double getBodyWeight(ExerciseType exerciseType) {
        return exerciseType == ExerciseType.BODYWEIGHT
                ? weightService.getNewestWeightEntry().weight()
                : 0;
    }

    private void validateTarget(Long muscleId, MuscleGroup muscleGroup) {
        if ((muscleId == null) == (muscleGroup == null)) {
            throw new IllegalArgumentException("Provide either muscleId or muscleGroup, but not both");
        }
    }

    private void validateNumWeeksBack(Integer numWeeksBack) {
        if (numWeeksBack == null || numWeeksBack < 1) {
            throw new IllegalArgumentException("numWeeksBack must be at least 1");
        }
    }

    private WeeklyDateRange calculateWeeklyDateRange(LocalDate date, int numWeeksBack) {
        LocalDate currentStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentEnd = currentStart.plusDays(6);
        return new WeeklyDateRange(currentStart, currentEnd, currentStart.minusWeeks(numWeeksBack));
    }

    private record WeeklyDateRange(
            LocalDate currentStart,
            LocalDate currentEnd,
            LocalDate earliestStart
    ) {}
}
