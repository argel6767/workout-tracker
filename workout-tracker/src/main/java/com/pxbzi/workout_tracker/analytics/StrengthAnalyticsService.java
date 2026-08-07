package com.pxbzi.workout_tracker.analytics;

import com.pxbzi.workout_tracker.analytics.calculations.StrengthCalculations;
import com.pxbzi.workout_tracker.analytics.models.ExerciseWeeklyOneRepMaxDto;
import com.pxbzi.workout_tracker.analytics.models.NormalizedStrengthAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.NormalizedStrengthPointDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleGroupDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseForMuscleDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExercisesOverviewDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyOneRepMaxAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyOneRepMaxChangeDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyOneRepMaxDto;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.muscles.MuscleService;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.workout_sets.WorkoutSetRepository;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StrengthAnalyticsService {

    private static final int AGE = 24;

    private final WorkoutService workoutService;
    private final WorkoutSetRepository workoutSetRepository;
    private final WeightService weightService;
    private final MuscleService muscleService;

    public StrongestExerciseByMuscleGroupDto getStrongestExerciseByMuscleGroup(
            MuscleGroup muscleGroup
    ) {
        WorkoutSet maxSet = workoutSetRepository.findSetsByMuscleGroup(muscleGroup).stream()
                .max(Comparator.comparingDouble(WorkoutSet::getWeight))
                .orElseThrow();
        Exercise exercise = maxSet.getWorkout().getExercise();
        return new StrongestExerciseByMuscleGroupDto(
                exercise.getId(),
                exercise.getName(),
                calculateEstimatedOneRepMax(maxSet, exercise.getExerciseType()),
                muscleGroup);
    }

    public StrongestExerciseByMuscleDto getStrongestExerciseByMuscle(Long muscleId) {
        Map<Exercise, List<WorkoutSet>> setsByExercise = workoutSetRepository
                .findSetsByMuscleId(muscleId).stream()
                .collect(Collectors.groupingBy(set -> set.getWorkout().getExercise()));

        return setsByExercise.entrySet().stream()
                .map(entry -> toStrongestExercise(entry.getKey(), entry.getValue()))
                .max(Comparator.comparingDouble(StrongestExerciseByMuscleDto::oneRepMax))
                .orElseThrow();
    }

    public StrongestExercisesOverviewDto getAllStrongestExercises(
            boolean includeMuscles,
            boolean includeMuscleGroups
    ) {
        if (!includeMuscles && !includeMuscleGroups) {
            throw new IllegalArgumentException(
                    "At least one strongest-exercise category must be included");
        }
        List<StrongestExerciseByMuscleGroupDto> muscleGroups = includeMuscleGroups
                ? Arrays.stream(MuscleGroup.values())
                        .map(this::getStrongestExerciseByMuscleGroupOrNull)
                        .filter(dto -> dto != null)
                        .toList()
                : List.of();
        List<StrongestExerciseForMuscleDto> muscles = includeMuscles
                ? muscleService.getAllMuscles().stream()
                        .map(this::getStrongestExerciseForMuscleOrNull)
                        .filter(dto -> dto != null)
                        .sorted(Comparator.comparing(StrongestExerciseForMuscleDto::muscleName))
                        .toList()
                : List.of();
        return new StrongestExercisesOverviewDto(muscleGroups, muscles);
    }

    public WeeklyOneRepMaxAnalysisDto getWeeklyOneRepMaxAnalysis(
            Long muscleId,
            LocalDate date,
            Integer numWeeksBack
    ) {
        validateNumWeeksBack(numWeeksBack);
        MuscleDto muscle = muscleService.getMuscle(muscleId);
        WeeklyDateRange dateRange = calculateWeeklyDateRange(date, numWeeksBack);
        List<Workout> workouts = workoutService.getWorkoutsByMuscleAndDateRange(
                muscleId, dateRange.earliestStart(), dateRange.currentEnd());
        List<ExerciseWeeklyOneRepMaxDto> exercises = workouts.stream()
                .collect(Collectors.groupingBy(workout -> workout.getExercise().getId()))
                .values().stream()
                .map(exerciseWorkouts -> toExerciseWeeklyOneRepMax(
                        exerciseWorkouts, dateRange.currentStart(), numWeeksBack))
                .sorted(Comparator.comparing(ExerciseWeeklyOneRepMaxDto::exerciseName))
                .toList();
        return new WeeklyOneRepMaxAnalysisDto(
                muscleId,
                muscle.name(),
                AGE,
                weightService.getNewestWeightEntry(),
                numWeeksBack,
                exercises);
    }

    public NormalizedStrengthAnalysisDto getNormalizedStrengthAnalysis(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) {
        validateTarget(muscleId, muscleGroup);
        if (muscleGroup == MuscleGroup.ARMS) {
            throw new IllegalArgumentException(
                    "ARMS normalized strength requires a biceps or triceps muscleId");
        }
        validateNumWeeksBack(numWeeksBack);
        String targetName = muscleId != null
                ? muscleService.getMuscle(muscleId).name()
                : muscleGroup.name();
        WeeklyDateRange dateRange = calculateWeeklyDateRange(date, numWeeksBack);
        List<Workout> workouts = muscleId != null
                ? workoutService.getWorkoutsByMuscleThroughDate(muscleId, dateRange.currentEnd())
                : workoutService.getWorkoutsByMuscleGroupThroughDate(
                        muscleGroup, dateRange.currentEnd());

        Map<LocalDate, Map<Long, List<Double>>> weeklyExerciseIndexes = new TreeMap<>();
        workouts.stream()
                .collect(Collectors.groupingBy(workout -> workout.getExercise().getId()))
                .values()
                .forEach(exerciseWorkouts -> addNormalizedExerciseSessions(
                        exerciseWorkouts,
                        dateRange.earliestStart(),
                        dateRange.currentEnd(),
                        weeklyExerciseIndexes));

        List<NormalizedStrengthPointDto> trend = weeklyExerciseIndexes.entrySet().stream()
                .map(entry -> toNormalizedStrengthPoint(entry.getKey(), entry.getValue()))
                .toList();
        return new NormalizedStrengthAnalysisDto(
                muscleId, muscleGroup, targetName, numWeeksBack, trend);
    }

    private StrongestExerciseByMuscleDto toStrongestExercise(
            Exercise exercise,
            List<WorkoutSet> sets
    ) {
        double maxOneRepMax = sets.stream()
                .mapToDouble(set -> calculateEstimatedOneRepMax(
                        set, exercise.getExerciseType()))
                .max()
                .orElse(0);
        return new StrongestExerciseByMuscleDto(
                exercise.getId(),
                exercise.getName(),
                maxOneRepMax,
                calculateAverageWeightPerRep(sets, exercise.getExerciseType()));
    }

    private StrongestExerciseByMuscleGroupDto getStrongestExerciseByMuscleGroupOrNull(
            MuscleGroup muscleGroup
    ) {
        try {
            return getStrongestExerciseByMuscleGroup(muscleGroup);
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private StrongestExerciseForMuscleDto getStrongestExerciseForMuscleOrNull(MuscleDto muscle) {
        try {
            StrongestExerciseByMuscleDto strongest = getStrongestExerciseByMuscle(muscle.id());
            return new StrongestExerciseForMuscleDto(
                    muscle.id(),
                    muscle.name(),
                    strongest.exerciseId(),
                    strongest.exerciseName(),
                    strongest.oneRepMax(),
                    strongest.avgWeightPerRep());
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private void addNormalizedExerciseSessions(
            List<Workout> workouts,
            LocalDate earliestStart,
            LocalDate currentEnd,
            Map<LocalDate, Map<Long, List<Double>>> weeklyExerciseIndexes
    ) {
        List<Workout> sortedWorkouts = workouts.stream()
                .sorted(Comparator.comparing(Workout::getWorkoutDate))
                .toList();
        Double baseline = sortedWorkouts.isEmpty()
                ? null
                : calculateSessionEstimatedOneRepMax(sortedWorkouts.getFirst());
        if (baseline == null || baseline <= 0 || !Double.isFinite(baseline)) {
            return;
        }
        for (Workout workout : sortedWorkouts) {
            if (workout.getWorkoutDate().isBefore(earliestStart)
                    || workout.getWorkoutDate().isAfter(currentEnd)) {
                continue;
            }
            Double sessionOneRepMax = calculateSessionEstimatedOneRepMax(workout);
            if (sessionOneRepMax == null || !Double.isFinite(sessionOneRepMax)) {
                continue;
            }
            LocalDate weekStart = workout.getWorkoutDate()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            weeklyExerciseIndexes
                    .computeIfAbsent(weekStart, ignored -> new TreeMap<>())
                    .computeIfAbsent(workout.getExercise().getId(), ignored -> new ArrayList<>())
                    .add((sessionOneRepMax / baseline) * 100);
        }
    }

    private Double calculateSessionEstimatedOneRepMax(Workout workout) {
        return workout.getWorkoutSets().stream()
                .mapToDouble(set -> calculateEstimatedOneRepMax(
                        set, workout.getExercise().getExerciseType()))
                .max()
                .stream()
                .boxed()
                .findFirst()
                .orElse(null);
    }

    private ExerciseWeeklyOneRepMaxDto toExerciseWeeklyOneRepMax(
            List<Workout> workouts,
            LocalDate currentStart,
            int numWeeksBack
    ) {
        Exercise exercise = workouts.getFirst().getExercise();
        List<WeeklyOneRepMaxDto> weeks = new ArrayList<>();
        for (int weeksAgo = numWeeksBack; weeksAgo >= 0; weeksAgo--) {
            LocalDate weekStart = currentStart.minusWeeks(weeksAgo);
            LocalDate weekEnd = weekStart.plusDays(6);
            WorkoutSet topSet = workouts.stream()
                    .filter(workout -> !workout.getWorkoutDate().isBefore(weekStart)
                            && !workout.getWorkoutDate().isAfter(weekEnd))
                    .flatMap(workout -> workout.getWorkoutSets().stream())
                    .max(Comparator.comparingDouble(set -> calculateEstimatedOneRepMax(
                            set, exercise.getExerciseType())))
                    .orElse(null);
            weeks.add(topSet == null
                    ? new WeeklyOneRepMaxDto(weekStart, weekEnd, null, null, null, null)
                    : new WeeklyOneRepMaxDto(
                            weekStart,
                            weekEnd,
                            calculateEstimatedOneRepMax(topSet, exercise.getExerciseType()),
                            SetDto.getSetDto(topSet),
                            topSet.getWorkout().getId(),
                            topSet.getWorkout().getWorkoutDate()));
        }

        List<WeeklyOneRepMaxChangeDto> changes = new ArrayList<>();
        for (int index = 1; index < weeks.size(); index++) {
            WeeklyOneRepMaxDto previous = weeks.get(index - 1);
            WeeklyOneRepMaxDto current = weeks.get(index);
            Double change = current.oneRepMax() == null || previous.oneRepMax() == null
                    ? null
                    : current.oneRepMax() - previous.oneRepMax();
            Double percentage = change == null || previous.oneRepMax() == 0
                    ? null
                    : (change / previous.oneRepMax()) * 100;
            changes.add(new WeeklyOneRepMaxChangeDto(current, previous, change, percentage));
        }
        return new ExerciseWeeklyOneRepMaxDto(
                exercise.getId(), exercise.getName(), changes);
    }

    private NormalizedStrengthPointDto toNormalizedStrengthPoint(
            LocalDate weekStart,
            Map<Long, List<Double>> valuesByExercise
    ) {
        List<Double> exerciseIndexes = valuesByExercise.values().stream()
                .map(values -> values.stream().mapToDouble(Double::doubleValue)
                        .average().orElseThrow())
                .toList();
        double average = exerciseIndexes.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseThrow();
        return new NormalizedStrengthPointDto(
                weekStart, weekStart.plusDays(6), average, exerciseIndexes.size());
    }

    private double calculateAverageWeightPerRep(
            List<WorkoutSet> sets,
            ExerciseType exerciseType
    ) {
        double volume = sets.stream().mapToDouble(set -> StrengthCalculations.setVolume(
                set.getReps(), set.getWeight(), exerciseType, getBodyWeight(exerciseType))).sum();
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
