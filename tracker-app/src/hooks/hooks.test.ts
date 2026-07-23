import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useQuery } from '@tanstack/react-query';
import { useGetAiAnalysisByExercise } from './useGetAiAnalysisByExercise';
import { useGetExerciseAnalytics } from './useGetExerciseAnalytics';
import { useGetExercises } from './useGetExercises';
import { useGetRelativeStrengthByExercise } from './useGetGetRelativeStrengthByExercise';
import { useGetMuscles } from './useGetMuscles';
import { useGetSetsBreakdownAiAnalysis } from './useGetSetsBreakdownAiAnalysis';
import { useGetSetsByMuscleGroup } from './useGetSetsByMuscleGroup';
import { useGetStrongestExercises } from './useGetStrongestExercises';
import { useGetStrongestExercisesAiAnalysis } from './useGetStrongestExercisesAiAnalysis';
import { useGetWeeklyOneRepMaxAiAnalysis } from './useGetWeeklyOneRepMaxAiAnalysis';
import { useGetWeeklyOneRepMaxAnalysis } from './useGetWeeklyOneRepMaxAnalysis';
import { useGetWeeklyVolumeAiAnalysis } from './useGetWeeklyVolumeAiAnalysis';
import { useGetWeeklyVolumeAnalysis } from './useGetWeeklyVolumeAnalysis';
import { useGetWeightsByDate } from './useGetWeightsByDate';
import { useGetWorkoutsBreakdownAiAnalysis } from './useGetWorkoutsBreakdownAiAnalysis';
import { useGetWorkoutsByExercise } from './useGetWorkoutsByExercise';
import { useGetWorkoutsByMuscleGroup } from './useGetWorkoutsByMuscleGroup';

vi.mock('@tanstack/react-query', () => ({ useQuery: vi.fn(() => ({ data: undefined })) }));

const latestOptions = () => vi.mocked(useQuery).mock.calls.at(-1)![0];

beforeEach(() => vi.mocked(useQuery).mockClear());

describe('React Query hook contracts', () => {
  it.each([
    ['exercises', () => useGetExercises(), ['exercises']],
    ['muscles', () => useGetMuscles(), ['muscles']],
    ['weights', () => useGetWeightsByDate(6), ['weights', 6]],
    ['workouts by muscle group', () => useGetWorkoutsByMuscleGroup(), ['workoutsByMuscleGroup']],
    ['sets by muscle group', () => useGetSetsByMuscleGroup(), ['setsByMuscleGroup']],
    ['workout breakdown AI', () => useGetWorkoutsBreakdownAiAnalysis(), ['workoutsBreakdownAiAnalysis']],
    ['set breakdown AI', () => useGetSetsBreakdownAiAnalysis(), ['setsBreakdownAiAnalysis']],
    ['strongest exercises', () => useGetStrongestExercises(), ['strongestExercises']],
    ['strongest exercises AI', () => useGetStrongestExercisesAiAnalysis(), ['strongestExercisesAiAnalysis']],
  ])('uses a stable key for %s', (_name, invoke, key) => {
    invoke();
    expect(latestOptions().queryKey).toEqual(key);
  });

  it.each([
    ['exercise analytics', () => useGetExerciseAnalytics(4, 3), ['exerciseAnalytics', 4, 3]],
    ['relative strength', () => useGetRelativeStrengthByExercise(4, 3), ['relativeStrength', 4, 3]],
    ['exercise AI', () => useGetAiAnalysisByExercise(4), ['aiAnalysis', 4]],
    ['workouts by exercise', () => useGetWorkoutsByExercise(4), ['workouts', 4]],
    ['weekly volume', () => useGetWeeklyVolumeAnalysis('CHEST', 8, 5), ['weeklyVolumeAnalysis', 'CHEST', 8, 5]],
    ['weekly volume AI', () => useGetWeeklyVolumeAiAnalysis('CHEST', 8, 5), ['weeklyVolumeAiAnalysis', 'CHEST', 8, 5]],
    ['weekly one-rep max', () => useGetWeeklyOneRepMaxAnalysis(8, 5), ['weeklyOneRepMaxAnalysis', 8, 5]],
    ['weekly one-rep max AI', () => useGetWeeklyOneRepMaxAiAnalysis(8, 5), ['weeklyOneRepMaxAiAnalysis', 8, 5]],
  ])('includes filter arguments in the %s key', (_name, invoke, key) => {
    invoke();
    expect(latestOptions().queryKey).toEqual(key);
    expect(latestOptions().enabled).toBe(true);
  });

  it.each([
    () => useGetExerciseAnalytics(-1, 3),
    () => useGetRelativeStrengthByExercise(4, 0),
    () => useGetAiAnalysisByExercise(-1),
    () => useGetWorkoutsByExercise(0),
    () => useGetWeeklyVolumeAnalysis('CHEST', undefined, 0),
    () => useGetWeeklyVolumeAiAnalysis('CHEST', undefined, 0),
    () => useGetWeeklyOneRepMaxAnalysis(undefined, 5),
    () => useGetWeeklyOneRepMaxAiAnalysis(undefined, 5),
  ])('disables a parameterized query when required filters are invalid', (invoke) => {
    invoke();
    expect(latestOptions().enabled).toBe(false);
  });
});
