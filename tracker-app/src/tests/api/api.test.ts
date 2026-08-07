import { beforeEach, describe, expect, it, vi } from 'vitest';
import { apiClient } from '../../api/apiConfig';
import * as muscles from '../../api/muscles';
import * as exercises from '../../api/exercise';
import * as workouts from '../../api/workouts';
import * as weights from '../../api/weights';
import * as analytics from '../../api/analytics';

vi.mock('../../api/apiConfig', () => ({ apiClient: { get: vi.fn(), post: vi.fn(), put: vi.fn(), delete: vi.fn() } }));

const response = { data: { id: 1 } };

beforeEach(() => {
  vi.mocked(apiClient.get).mockResolvedValue(response);
  vi.mocked(apiClient.post).mockResolvedValue(response);
  vi.mocked(apiClient.put).mockResolvedValue(response);
  vi.mocked(apiClient.delete).mockResolvedValue(response);
});

describe('muscle API', () => {
  it('uses the expected endpoints for every operation', async () => {
    const created = { name: 'Biceps', muscleGroup: 'ARMS' as const };
    const updated = { name: 'Triceps', muscleGroup: 'ARMS' as const };

    await expect(muscles.getMuscles()).resolves.toEqual(response.data);
    await muscles.getMuscle('1');
    await muscles.createMuscle(created);
    await muscles.updateMuscle('1', updated);
    await muscles.deleteMuscle('1');

    expect(apiClient.get).toHaveBeenNthCalledWith(1, '/v1/muscles');
    expect(apiClient.get).toHaveBeenNthCalledWith(2, '/v1/muscles/1');
    expect(apiClient.post).toHaveBeenCalledWith('/v1/muscles', created);
    expect(apiClient.put).toHaveBeenCalledWith('/v1/muscles/1', updated);
    expect(apiClient.delete).toHaveBeenCalledWith('/v1/muscles/1');
  });
});

describe('exercise API', () => {
  it('uses the expected endpoints for every operation', async () => {
    const payload = { name: 'Curl', description: '', musclesWorked: [1], primaryMuscleId: 1, exerciseType: 'FREE_WEIGHT' as const };

    await exercises.getExercises();
    await exercises.getExercise('2');
    await exercises.createExercise(payload);
    await exercises.updateExercise('2', payload);
    await exercises.deleteExercise('2');

    expect(apiClient.get).toHaveBeenNthCalledWith(1, '/v1/exercises');
    expect(apiClient.get).toHaveBeenNthCalledWith(2, '/v1/exercises/2');
    expect(apiClient.post).toHaveBeenCalledWith('/v1/exercises', payload);
    expect(apiClient.put).toHaveBeenCalledWith('/v1/exercises/2', payload);
    expect(apiClient.delete).toHaveBeenCalledWith('/v1/exercises/2');
  });
});

describe('workout API', () => {
  it('uses the expected endpoints and query parameters for every operation', async () => {
    const payload = { exerciseId: 2, sets: [{ weight: 100, reps: 8 }], workoutDate: '2026-07-22' };

    await workouts.getWorkouts();
    await workouts.getWorkoutsByExercise(2);
    await workouts.getWorkout('3');
    await workouts.createWorkout(payload);
    await workouts.updateWorkout('3', payload);
    await workouts.deleteWorkout('3');

    expect(apiClient.get).toHaveBeenNthCalledWith(1, '/v1/workouts');
    expect(apiClient.get).toHaveBeenNthCalledWith(2, '/v1/workouts/exercises', { params: { exerciseId: 2 } });
    expect(apiClient.get).toHaveBeenNthCalledWith(3, '/v1/workouts/3');
    expect(apiClient.post).toHaveBeenCalledWith('/v1/workouts', payload);
    expect(apiClient.put).toHaveBeenCalledWith('/v1/workouts/3', payload);
    expect(apiClient.delete).toHaveBeenCalledWith('/v1/workouts/3');
  });
});

describe('weight API', () => {
  it('uses the expected endpoints and query parameters for every operation', async () => {
    const payload = { id: 4, weight: 175, entryDate: '2026-07-22' };

    await weights.getWeightsByDate(6);
    await weights.getWeight(4);
    await weights.createWeight(payload);
    await weights.updateWeight(payload);
    await weights.deleteWeight(4);

    expect(apiClient.get).toHaveBeenNthCalledWith(1, '/v1/weights/dates', { params: { numMonthsBack: 6 } });
    expect(apiClient.get).toHaveBeenNthCalledWith(2, '/v1/weights/4');
    expect(apiClient.post).toHaveBeenCalledWith('/v1/weights', payload);
    expect(apiClient.put).toHaveBeenCalledWith('/v1/weights/4', payload);
    expect(apiClient.delete).toHaveBeenCalledWith('/v1/weights/4');
  });
});

describe('analytics API', () => {
  it('covers exercise progress, breakdown, strongest-exercise, and AI endpoints', async () => {
    await analytics.getExerciseAnalytics(2, 3);
    await analytics.getRelativeStrengthByExercise(2, 3);
    await analytics.getAiAnalysisByExercise(2);
    await analytics.getWorkoutsByMuscleGroup();
    await analytics.getSetsByMuscleGroup();
    await analytics.getWorkoutsBreakdownAiAnalysis();
    await analytics.getSetsBreakdownAiAnalysis();
    await analytics.getStrongestExercises();
    await analytics.getStrongestExercisesAiAnalysis();

    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/exercise', { params: { exerciseId: 2, numOfMonthsBack: 3 } });
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/relative-strength', { params: { exerciseId: 2, numOfMonthsBack: 3 } });
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/ai-analysis', { params: { exerciseId: 2 } });
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/workouts-breakdown');
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/sets-breakdown');
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/workouts-breakdown/ai-analysis');
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/sets-breakdown/ai-analysis');
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/strongest-exercises');
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/strongest-exercises/ai-analysis');
  });

  it('builds weekly-analysis parameters for groups and individual muscles', async () => {
    await analytics.getWeeklyVolumeAnalysis('CHEST', undefined, 4, '2026-07-22');
    await analytics.getWeeklyVolumeAiAnalysis('CHEST', 9, 4, '2026-07-22');
    await analytics.getWeeklyOneRepMaxAnalysis(9, 4, '2026-07-22');
    await analytics.getWeeklyOneRepMaxAiAnalysis(9, 4, '2026-07-22');

    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/weekly-volume', { params: { muscleGroup: 'CHEST', date: '2026-07-22', numWeeksBack: 4 } });
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/weekly-volume/ai-analysis', { params: { muscleId: 9, date: '2026-07-22', numWeeksBack: 4 } });
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/weekly-one-rep-max', { params: { muscleId: 9, date: '2026-07-22', numWeeksBack: 4 } });
    expect(apiClient.get).toHaveBeenCalledWith('/v1/analytics/progress/weekly-one-rep-max/ai-analysis', { params: { muscleId: 9, date: '2026-07-22', numWeeksBack: 4 } });
  });

  it('builds normalized-strength parameters for groups and individual muscles', async () => {
    await analytics.getNormalizedStrengthAnalysis('BACK', undefined, 8, '2026-08-05');
    await analytics.getNormalizedStrengthAnalysis('ARMS', 9, 8, '2026-08-05');

    expect(apiClient.get).toHaveBeenNthCalledWith(1, '/v1/analytics/progress/normalized-strength', {
      params: { muscleGroup: 'BACK', date: '2026-08-05', numWeeksBack: 8 },
    });
    expect(apiClient.get).toHaveBeenNthCalledWith(2, '/v1/analytics/progress/normalized-strength', {
      params: { muscleId: 9, date: '2026-08-05', numWeeksBack: 8 },
    });
  });
});

describe('API errors', () => {
  it.each([
    ['muscles', () => muscles.getMuscles()],
    ['exercises', () => exercises.getExercises()],
    ['workouts', () => workouts.getWorkouts()],
    ['weights', () => weights.getWeightsByDate(3)],
    ['analytics', () => analytics.getStrongestExercises()],
  ])('propagates %s request failures to the caller', async (_domain, request) => {
    const failure = new Error('network unavailable');
    vi.mocked(apiClient.get).mockRejectedValueOnce(failure);
    await expect(request()).rejects.toBe(failure);
  });
});
