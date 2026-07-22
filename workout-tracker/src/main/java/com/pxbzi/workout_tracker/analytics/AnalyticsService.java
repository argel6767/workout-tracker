package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pxbzi.workout_tracker.analytics.models.*;
import com.pxbzi.workout_tracker.exercises.models.Exercise;
import com.pxbzi.workout_tracker.exercises.models.ExerciseDTO;
import com.pxbzi.workout_tracker.exercises.models.ExerciseType;
import com.pxbzi.workout_tracker.gemini.GeminiService;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import com.pxbzi.workout_tracker.gemini.models.ExerciseProgressionDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import com.pxbzi.workout_tracker.muscles.MuscleService;
import com.pxbzi.workout_tracker.muscles.models.MuscleDto;
import com.pxbzi.workout_tracker.weights.WeightService;
import com.pxbzi.workout_tracker.weights.models.WeightDto;
import com.pxbzi.workout_tracker.workout_sets.WorkoutSetRepository;
import com.pxbzi.workout_tracker.workout_sets.models.SetDto;
import com.pxbzi.workout_tracker.workout_sets.models.WorkoutSet;
import com.pxbzi.workout_tracker.workouts.WorkoutService;
import com.pxbzi.workout_tracker.workouts.models.Workout;
import com.pxbzi.workout_tracker.workouts.models.WorkoutDto;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final MuscleService muscleService;
    private static final int AGE = 24;
    private static final String SEX = "male";

    public AnalyticsDto getWorkoutAnalyticsByExerciseId(
        Long exerciseId,
        Integer numOfMonthsBack
    ) {
        List<Workout> workouts = workoutService.getWorkoutsByExerciseId(
            exerciseId,
            numOfMonthsBack
        );
        
        ExerciseType exerciseType = workouts.getLast().getExercise().getExerciseType();

        List<DataPoint<LocalDate, Double>> oneRepMaxes = workouts
            .stream()
            .map(workout ->
                new DataPoint<>(
                    workout.getWorkoutDate(),
                    calculateEstimatedOneRepMax(getTopSet(workout), exerciseType)
                )
            )
            .toList();

        List<DataPoint<LocalDate, Double>> avgWeightPerReps = workouts
            .stream()
            .map(workout -> {
                List<WorkoutSet> workoutSets = workout.getWorkoutSets();
                double avgWeightPerRep = calculateAvgWeightPerRep(workoutSets, exerciseType);
                return new DataPoint<>(workout.getWorkoutDate(), avgWeightPerRep);
            })
            .toList();

        List<DataPoint<LocalDate, Double>> totalVolumes = workouts
            .stream()
            .map(workout -> calculateTotalVolume(workout, exerciseType))
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
        Exercise exercise = maxSet.getWorkout().getExercise();
        double oneRepMax = calculateEstimatedOneRepMax(maxSet, exercise.getExerciseType());
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
                    .mapToDouble(set -> calculateEstimatedOneRepMax(set, exercise.getExerciseType()))
                    .max()
                    .orElse(0);

                double avgWeightPerRep = calculateAvgWeightPerRep(exerciseSets, exercise.getExerciseType());

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

    public List<RelativeStrengthDto> getRelativeStrength(int numMonthsBack, Long exerciseId) {
        List<WeightDto> weights = weightService.getAllWeightsInDateRange(
            numMonthsBack
        );
        List<Workout> workouts = workoutService.getWorkoutsByExerciseId(
            exerciseId,
            numMonthsBack
        );
        
        ExerciseType exerciseType = workouts.getFirst().getExercise().getExerciseType();

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
        Set<LocalDate> allDates = new TreeSet<>();
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

                double oneRepMax = calculateEstimatedOneRepMax(workoutSet, exerciseType);
                double weight = weightDto.weight();
                double relativeStrength = (oneRepMax / weight) * 100;
                return new RelativeStrengthDto(weight, oneRepMax, relativeStrength, date);
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
            
            List<WorkoutDto> lastMonthsWorkouts = workoutService.getWorkoutsByExerciseId(exerciseId, 1)
                                                    .stream()
                                                    .map(WorkoutDto::getWorkoutDto)
                                                    .toList();
                                                    
        ExerciseProgressionDto dto =
            ExerciseProgressionDto.getExerciseProgressionDto(
                workout,
                topSet,
                lastMonthsWorkouts,
                weight,
                AGE,
                SEX
            );

        String dtoStringfy = objectMapper.writeValueAsString(dto);
        return geminiService.getChatResponseDto(dtoStringfy);
    }
    
    public List<DataPoint<String, Integer>> aggregateWorkoutsByMuscle() {
        List<WorkoutDto> workouts = workoutService.getAllWorkouts();
        List<DataPoint<String, Integer>> dataPoints = workouts.stream()
            .collect(Collectors.groupingBy(workout -> workout.exercise().primaryMuscleGroup(), Collectors.counting()))
            .entrySet().stream()
            .map(entry -> new DataPoint<>(entry.getKey(), entry.getValue().intValue()))
            .toList();
        return dataPoints;
    }
    
    public List<DataPoint<String, Integer>> aggregateSetsByMuscle() {
        List<WorkoutSet> workoutSets = workoutSetRepository.findAll();
        List<DataPoint<String, Integer>> dataPoints = workoutSets.stream()
            .collect(Collectors.groupingBy(workoutSet -> {
                WorkoutSet set = (WorkoutSet) workoutSet;
                Exercise exercise = set.getWorkout().getExercise();
                MuscleGroup primaryMuscleGroup = ExerciseDTO.findPrimaryMuscleGroup(exercise);
                return primaryMuscleGroup.name();
            }, Collectors.counting()))
            .entrySet().stream()
            .map(entry -> new DataPoint<>(entry.getKey(), entry.getValue().intValue()))
            .toList();
        return dataPoints;
    }

    public ChatResponseDto analyzeWorkoutsBreakdown() throws JsonProcessingException {
        List<DataPoint<String, Integer>> breakdown = aggregateWorkoutsByMuscle();
        String prompt = "Analyze this workout-count distribution by muscle group in no more than five short sentences. "
                + "Identify the most important training-balance trend and finish with one actionable recommendation. "
                + "Do not list every value or invent missing context. Data: "
                + objectMapper.writeValueAsString(breakdown);
        return geminiService.getConciseChatResponseDto(prompt);
    }

    public ChatResponseDto analyzeSetsBreakdown() throws JsonProcessingException {
        List<DataPoint<String, Integer>> breakdown = aggregateSetsByMuscle();
        String prompt = "Analyze this set-count distribution by muscle group in no more than five short sentences. "
                + "Identify the most important training-volume balance trend and finish with one actionable recommendation. "
                + "Do not list every value or invent missing context. Data: "
                + objectMapper.writeValueAsString(breakdown);
        return geminiService.getConciseChatResponseDto(prompt);
    }
    
    public List<DataPoint<String, Double>> aggregateTotalVolumeByMonth() {
        List<WorkoutDto> workouts = workoutService.getAllWorkouts();
    
        return workouts.stream()
            .collect(Collectors.groupingBy(
                workout -> YearMonth.from(workout.workoutDate()),
                Collectors.summingDouble(workout -> calculateTotalVolume(workout, workout.exercise().exerciseType()))
            ))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> new DataPoint<>(
                entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) 
                    + " " + entry.getKey().getYear(),
                entry.getValue()
            ))
            .toList();
    }

    public WeeklyVolumeAnalysisDto getWeeklyVolumeAnalysis(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) {
        if ((muscleId == null) == (muscleGroup == null)) {
            throw new IllegalArgumentException("Provide either muscleId or muscleGroup, but not both");
        }
        if (numWeeksBack == null || numWeeksBack < 1) {
            throw new IllegalArgumentException("numWeeksBack must be at least 1");
        }

        String targetName;
        if (muscleId != null) {
            MuscleDto muscle = muscleService.getMuscle(muscleId);
            targetName = muscle.name();
        } else {
            targetName = muscleGroup.name();
        }

        LocalDate currentStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentEnd = currentStart.plusDays(6);
        LocalDate earliestStart = currentStart.minusWeeks(numWeeksBack);
        List<Workout> workouts = muscleId != null
                ? workoutService.getWorkoutsByMuscleAndDateRange(muscleId, earliestStart, currentEnd)
                : workoutService.getWorkoutsByMuscleGroupAndDateRange(muscleGroup, earliestStart, currentEnd);

        List<WeeklyVolumeDto> weeks = new ArrayList<>();
        for (int weeksAgo = numWeeksBack; weeksAgo >= 0; weeksAgo--) {
            LocalDate weekStart = currentStart.minusWeeks(weeksAgo);
            LocalDate weekEnd = weekStart.plusDays(6);
            List<Workout> weeklyWorkouts = workouts.stream()
                    .filter(workout -> !workout.getWorkoutDate().isBefore(weekStart)
                            && !workout.getWorkoutDate().isAfter(weekEnd))
                    .toList();
            weeks.add(toWeeklyVolume(weekStart, weekEnd, weeklyWorkouts));
        }

        List<WeeklyVolumeChangeDto> weeklyChanges = new ArrayList<>();
        for (int index = 1; index < weeks.size(); index++) {
            WeeklyVolumeDto previousWeek = weeks.get(index - 1);
            WeeklyVolumeDto currentWeek = weeks.get(index);
            double volumeChange = currentWeek.totalVolume() - previousWeek.totalVolume();
            Double percentageChange = previousWeek.totalVolume() == 0 ? null
                    : (volumeChange / previousWeek.totalVolume()) * 100;
            weeklyChanges.add(new WeeklyVolumeChangeDto(
                    currentWeek, previousWeek, volumeChange, percentageChange));
        }

        return new WeeklyVolumeAnalysisDto(
                muscleId, muscleGroup, targetName, numWeeksBack, weeklyChanges);
    }

    public ChatResponseDto analyzeWeeklyVolumeWithAi(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) throws JsonProcessingException {
        WeeklyVolumeAnalysisDto analysis = getWeeklyVolumeAnalysis(
                muscleId, muscleGroup, date, numWeeksBack);
        String prompt = "Summarize this week-over-week workout volume data in no more than five short sentences. "
                + "State the main trend and give one actionable recommendation. "
                + "Do not list every week, recalculate values, or invent missing data. Data: "
                + objectMapper.writeValueAsString(analysis);
        return geminiService.getConciseChatResponseDto(prompt);
    }

    public WeeklyOneRepMaxAnalysisDto getWeeklyOneRepMaxAnalysis(
            Long muscleId,
            LocalDate date,
            Integer numWeeksBack
    ) {
        if (numWeeksBack == null || numWeeksBack < 1) {
            throw new IllegalArgumentException("numWeeksBack must be at least 1");
        }

        MuscleDto muscle = muscleService.getMuscle(muscleId);
        LocalDate currentStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate currentEnd = currentStart.plusDays(6);
        LocalDate earliestStart = currentStart.minusWeeks(numWeeksBack);
        List<Workout> workouts = workoutService.getWorkoutsByMuscleAndDateRange(
                muscleId, earliestStart, currentEnd);

        Map<Long, List<Workout>> workoutsByExercise = workouts.stream()
                .collect(Collectors.groupingBy(workout -> workout.getExercise().getId()));
        List<ExerciseWeeklyOneRepMaxDto> exercises = workoutsByExercise.values().stream()
                .map(exerciseWorkouts -> toExerciseWeeklyOneRepMax(
                        exerciseWorkouts, currentStart, numWeeksBack))
                .sorted(Comparator.comparing(ExerciseWeeklyOneRepMaxDto::exerciseName))
                .toList();

        return new WeeklyOneRepMaxAnalysisDto(
                muscleId,
                muscle.name(),
                AGE,
                weightService.getNewestWeightEntry(),
                numWeeksBack,
                exercises
        );
    }

    public ChatResponseDto analyzeWeeklyOneRepMaxWithAi(
            Long muscleId,
            LocalDate date,
            Integer numWeeksBack
    ) throws JsonProcessingException {
        WeeklyOneRepMaxAnalysisDto analysis = getWeeklyOneRepMaxAnalysis(
                muscleId, date, numWeeksBack);
        String prompt = "Summarize this exercise-specific week-over-week estimated one-rep-max data in no more "
                + "than five short sentences. State the main strength trend and give one actionable recommendation. "
                + "Do not list every exercise or week. Treat missing weeks as missing data and do not invent data. Data: "
                + objectMapper.writeValueAsString(analysis);
        return geminiService.getConciseChatResponseDto(prompt);
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
                    .max(Comparator.comparingDouble(set ->
                            calculateEstimatedOneRepMax(set, exercise.getExerciseType())))
                    .orElse(null);
            weeks.add(topSet == null
                    ? new WeeklyOneRepMaxDto(weekStart, weekEnd, null, null, null, null)
                    : new WeeklyOneRepMaxDto(
                            weekStart,
                            weekEnd,
                            calculateEstimatedOneRepMax(topSet, exercise.getExerciseType()),
                            SetDto.getSetDto(topSet),
                            topSet.getWorkout().getId(),
                            topSet.getWorkout().getWorkoutDate()
                    ));
        }

        List<WeeklyOneRepMaxChangeDto> weeklyChanges = new ArrayList<>();
        for (int index = 1; index < weeks.size(); index++) {
            WeeklyOneRepMaxDto previousWeek = weeks.get(index - 1);
            WeeklyOneRepMaxDto currentWeek = weeks.get(index);
            Double change = currentWeek.oneRepMax() == null || previousWeek.oneRepMax() == null
                    ? null
                    : currentWeek.oneRepMax() - previousWeek.oneRepMax();
            Double percentageChange = change == null || previousWeek.oneRepMax() == 0
                    ? null
                    : (change / previousWeek.oneRepMax()) * 100;
            weeklyChanges.add(new WeeklyOneRepMaxChangeDto(
                    currentWeek, previousWeek, change, percentageChange));
        }
        return new ExerciseWeeklyOneRepMaxDto(
                exercise.getId(), exercise.getName(), weeklyChanges);
    }

    private WeeklyVolumeDto toWeeklyVolume(LocalDate startDate, LocalDate endDate, List<Workout> workouts) {
        double totalVolume = workouts.stream().mapToDouble(workout -> calculateTotalVolume(
                WorkoutDto.getWorkoutDto(workout), workout.getExercise().getExerciseType())).sum();
        List<WorkoutDto> workoutDtos = workouts.stream().map(WorkoutDto::getWorkoutDto).toList();
        return new WeeklyVolumeDto(startDate, endDate, workoutDtos, totalVolume);
    }

    private WorkoutSet getTopSet(Workout workout) {
        return workout
            .getWorkoutSets()
            .stream()
            .max(Comparator.comparingDouble(WorkoutSet::getWeight))
            .orElseThrow();
    }

    private DataPoint<LocalDate, Double> calculateTotalVolume(Workout workout, ExerciseType exerciseType) {
        List<WorkoutSet> workoutSets = workout.getWorkoutSets();
        double volume = 0;

        for (WorkoutSet workoutSet : workoutSets) {
            double weight = exerciseType == ExerciseType.BODYWEIGHT
                ? getNewestWeightEntry() + workoutSet.getWeight()
                : workoutSet.getWeight();
            volume += (workoutSet.getReps() * weight);
        }
        return new DataPoint<>(workout.getWorkoutDate(), volume);
    }
    
    private double calculateTotalVolume(WorkoutDto workout, ExerciseType exerciseType) {
        List<SetDto> sets = workout.sets();
        double volume = 0;

        for (SetDto set : sets) {
            double weight = exerciseType == ExerciseType.BODYWEIGHT
                ? getNewestWeightEntry() + set.weight()
                : set.weight();
            volume += (set.reps() * weight);
        }
        return volume;
    }

    private double calculateAvgWeightPerRep(List<WorkoutSet> workoutSets, ExerciseType exerciseType) {
        double volume = 0;
        int reps = 0;

        for (WorkoutSet workoutSet : workoutSets) {
            double weight = exerciseType == ExerciseType.BODYWEIGHT
                ? getNewestWeightEntry() + workoutSet.getWeight()
                : workoutSet.getWeight();
            volume += (workoutSet.getReps() * weight);
            reps += workoutSet.getReps();
        }

        return volume / reps;
    }

    private double calculateEstimatedOneRepMax(WorkoutSet maxWorkoutSet, ExerciseType exerciseType) {
        double weight = exerciseType == ExerciseType.BODYWEIGHT
            ? getNewestWeightEntry() + maxWorkoutSet.getWeight()
            : maxWorkoutSet.getWeight();
        return weight * (1 + ((double) maxWorkoutSet.getReps() / 30));
    }

    private Pair<LocalDate, LocalDate> calculateDateRange(int numMonthsBack) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(numMonthsBack);
        return Pair.of(startDate, endDate);
    }
    
    private double getNewestWeightEntry() {
        return weightService.getNewestWeightEntry().weight();
    }
}
