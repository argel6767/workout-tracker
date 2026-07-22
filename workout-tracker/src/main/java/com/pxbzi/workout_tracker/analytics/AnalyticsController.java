package com.pxbzi.workout_tracker.analytics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pxbzi.workout_tracker.analytics.models.AnalyticsDto;
import com.pxbzi.workout_tracker.analytics.models.DataPoint;
import com.pxbzi.workout_tracker.analytics.models.RelativeStrengthDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExerciseByMuscleGroupDto;
import com.pxbzi.workout_tracker.analytics.models.StrongestExercisesOverviewDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyVolumeAnalysisDto;
import com.pxbzi.workout_tracker.analytics.models.WeeklyOneRepMaxAnalysisDto;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import com.pxbzi.workout_tracker.muscles.models.MuscleGroup;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/analytics")
@Data
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/progress/exercise")
    public AnalyticsDto getWorkoutAnalyticsByExerciseId(@RequestParam Long exerciseId, @RequestParam Integer numOfMonthsBack) {
        return analyticsService.getWorkoutAnalyticsByExerciseId(exerciseId, numOfMonthsBack);
    }

    @GetMapping("/progress/relative-strength")
    public List<RelativeStrengthDto> getRelativeStrengthByExerciseId( @RequestParam Integer numOfMonthsBack, @RequestParam Long exerciseId) {
        return analyticsService.getRelativeStrength(numOfMonthsBack, exerciseId);
    }

    @GetMapping("/progress/ai-analysis")
    public ChatResponseDto getAiAnalysisByExerciseId(@RequestParam Long exerciseId) throws JsonProcessingException {
        return analyticsService.analyzeExerciseProgression(exerciseId);
    }
    
    @GetMapping("/progress/workouts-breakdown")
    public List<DataPoint<String, Integer>> getWorkoutsByMuscleGroup() {
        return analyticsService.aggregateWorkoutsByMuscle();
    }

    @GetMapping("/progress/workouts-breakdown/ai-analysis")
    public ChatResponseDto getWorkoutsBreakdownAiAnalysis() throws JsonProcessingException {
        return analyticsService.analyzeWorkoutsBreakdown();
    }
    
    @GetMapping("/progress/sets-breakdown")
    public List<DataPoint<String, Integer>> getSetsByMuscleGroup() {
        return analyticsService.aggregateSetsByMuscle();
    }

    @GetMapping("/progress/sets-breakdown/ai-analysis")
    public ChatResponseDto getSetsBreakdownAiAnalysis() throws JsonProcessingException {
        return analyticsService.analyzeSetsBreakdown();
    }
    
    @GetMapping("/progress/volume-breakdown")
    public List<DataPoint<String, Double>> getTotalVolumeByMuscleGroup() {
        return analyticsService.aggregateTotalVolumeByMonth();
    }

    @GetMapping("/progress/weekly-volume")
    public WeeklyVolumeAnalysisDto getWeeklyVolumeAnalysis(
            @RequestParam(required = false) Long muscleId,
            @RequestParam(required = false) MuscleGroup muscleGroup,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "5") Integer numWeeksBack
    ) {
        return analyticsService.getWeeklyVolumeAnalysis(muscleId, muscleGroup, date, numWeeksBack);
    }

    @GetMapping("/progress/weekly-volume/ai-analysis")
    public ChatResponseDto getWeeklyVolumeAiAnalysis(
            @RequestParam(required = false) Long muscleId,
            @RequestParam(required = false) MuscleGroup muscleGroup,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "5") Integer numWeeksBack
    ) throws JsonProcessingException {
        return analyticsService.analyzeWeeklyVolumeWithAi(muscleId, muscleGroup, date, numWeeksBack);
    }

    @GetMapping("/progress/weekly-one-rep-max")
    public WeeklyOneRepMaxAnalysisDto getWeeklyOneRepMaxAnalysis(
            @RequestParam Long muscleId,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "5") Integer numWeeksBack
    ) {
        return analyticsService.getWeeklyOneRepMaxAnalysis(muscleId, date, numWeeksBack);
    }

    @GetMapping("/progress/weekly-one-rep-max/ai-analysis")
    public ChatResponseDto getWeeklyOneRepMaxAiAnalysis(
            @RequestParam Long muscleId,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "5") Integer numWeeksBack
    ) throws JsonProcessingException {
        return analyticsService.analyzeWeeklyOneRepMaxWithAi(muscleId, date, numWeeksBack);
    }

    @GetMapping("/strongest-exercises/muscle-groups/{muscleGroup}")
    public StrongestExerciseByMuscleGroupDto getStrongestExerciseByMuscleGroup(@PathVariable MuscleGroup muscleGroup) {
        return analyticsService.getStrongestExerciseByMuscleGroup(muscleGroup);
    }

    @GetMapping("/strongest-exercises")
    public StrongestExercisesOverviewDto getAllStrongestExercises(
            @RequestParam(defaultValue = "true") boolean includeMuscles,
            @RequestParam(defaultValue = "true") boolean includeMuscleGroups
    ) {
        return analyticsService.getAllStrongestExercises(includeMuscles, includeMuscleGroups);
    }

    @GetMapping("/strongest-exercises/ai-analysis")
    public ChatResponseDto getAllStrongestExercisesAiAnalysis(
            @RequestParam(defaultValue = "true") boolean includeMuscles,
            @RequestParam(defaultValue = "true") boolean includeMuscleGroups
    ) throws JsonProcessingException {
        return analyticsService.analyzeAllStrongestExercises(includeMuscles, includeMuscleGroups);
    }

    @GetMapping("/strongest-exercises/muscles/{muscleId}")
    public StrongestExerciseByMuscleDto getStrongestExerciseByMuscle(@PathVariable Long muscleId) {
        return analyticsService.getStrongestExerciseByMuscle(muscleId);
    }
}
