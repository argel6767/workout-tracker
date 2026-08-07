package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pxbzi.workout_tracker.analytics.models.AnalyticsDto;
import com.pxbzi.workout_tracker.analytics.models.DataPoint;
import com.pxbzi.workout_tracker.analytics.models.NormalizedStrengthAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.RelativeStrengthDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleGroupDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExercisesOverviewDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyOneRepMaxAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyVolumeAnalysisDto;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AnalyticsService {

    private final ExerciseProgressAnalyticsService exerciseProgressAnalyticsService;
    private final VolumeAnalyticsService volumeAnalyticsService;
    private final StrengthAnalyticsService strengthAnalyticsService;
    private final AnalyticsInsightService analyticsInsightService;

    public AnalyticsService(
            ExerciseProgressAnalyticsService exerciseProgressAnalyticsService,
            VolumeAnalyticsService volumeAnalyticsService,
            StrengthAnalyticsService strengthAnalyticsService,
            AnalyticsInsightService analyticsInsightService
    ) {
        this.exerciseProgressAnalyticsService = exerciseProgressAnalyticsService;
        this.volumeAnalyticsService = volumeAnalyticsService;
        this.strengthAnalyticsService = strengthAnalyticsService;
        this.analyticsInsightService = analyticsInsightService;
    }

    public AnalyticsDto getWorkoutAnalyticsByExerciseId(Long exerciseId, Integer numOfMonthsBack) {
        return exerciseProgressAnalyticsService.getWorkoutAnalyticsByExerciseId(
                exerciseId, numOfMonthsBack);
    }

    public List<RelativeStrengthDto> getRelativeStrength(int numMonthsBack, Long exerciseId) {
        return exerciseProgressAnalyticsService.getRelativeStrength(numMonthsBack, exerciseId);
    }

    public ChatResponseDto analyzeExerciseProgression(Long exerciseId)
            throws JsonProcessingException {
        return analyticsInsightService.analyzeExerciseProgression(exerciseId);
    }

    public List<DataPoint<String, Integer>> aggregateWorkoutsByMuscle() {
        return volumeAnalyticsService.aggregateWorkoutsByMuscle();
    }

    public List<DataPoint<String, Integer>> aggregateSetsByMuscle() {
        return volumeAnalyticsService.aggregateSetsByMuscle();
    }

    public ChatResponseDto analyzeWorkoutsBreakdown() throws JsonProcessingException {
        return analyticsInsightService.analyzeWorkoutsBreakdown();
    }

    public ChatResponseDto analyzeSetsBreakdown() throws JsonProcessingException {
        return analyticsInsightService.analyzeSetsBreakdown();
    }

    public List<DataPoint<String, Double>> aggregateTotalVolumeByMonth() {
        return volumeAnalyticsService.aggregateTotalVolumeByMonth();
    }

    public WeeklyVolumeAnalysisDto getWeeklyVolumeAnalysis(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) {
        return volumeAnalyticsService.getWeeklyVolumeAnalysis(
                muscleId, muscleGroup, date, numWeeksBack);
    }

    public ChatResponseDto analyzeWeeklyVolumeWithAi(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) throws JsonProcessingException {
        return analyticsInsightService.analyzeWeeklyVolume(
                muscleId, muscleGroup, date, numWeeksBack);
    }

    public StrongestExerciseByMuscleGroupDto getStrongestExerciseByMuscleGroup(
            MuscleGroup muscleGroup
    ) {
        return strengthAnalyticsService.getStrongestExerciseByMuscleGroup(muscleGroup);
    }

    public StrongestExerciseByMuscleDto getStrongestExerciseByMuscle(Long muscleId) {
        return strengthAnalyticsService.getStrongestExerciseByMuscle(muscleId);
    }

    public StrongestExercisesOverviewDto getAllStrongestExercises(
            boolean includeMuscles,
            boolean includeMuscleGroups
    ) {
        return strengthAnalyticsService.getAllStrongestExercises(
                includeMuscles, includeMuscleGroups);
    }

    public ChatResponseDto analyzeAllStrongestExercises(
            boolean includeMuscles,
            boolean includeMuscleGroups
    ) throws JsonProcessingException {
        return analyticsInsightService.analyzeAllStrongestExercises(
                includeMuscles, includeMuscleGroups);
    }

    public WeeklyOneRepMaxAnalysisDto getWeeklyOneRepMaxAnalysis(
            Long muscleId,
            LocalDate date,
            Integer numWeeksBack
    ) {
        return strengthAnalyticsService.getWeeklyOneRepMaxAnalysis(
                muscleId, date, numWeeksBack);
    }

    public ChatResponseDto analyzeWeeklyOneRepMaxWithAi(
            Long muscleId,
            LocalDate date,
            Integer numWeeksBack
    ) throws JsonProcessingException {
        return analyticsInsightService.analyzeWeeklyOneRepMax(
                muscleId, date, numWeeksBack);
    }

    public NormalizedStrengthAnalysisDto getNormalizedStrengthAnalysis(
            Long muscleId,
            MuscleGroup muscleGroup,
            LocalDate date,
            Integer numWeeksBack
    ) {
        return strengthAnalyticsService.getNormalizedStrengthAnalysis(
                muscleId, muscleGroup, date, numWeeksBack);
    }
}
