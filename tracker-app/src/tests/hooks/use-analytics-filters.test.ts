import { act, renderHook } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { useAnalyticsFilters } from '../../hooks/useAnalyticsFilters';

const muscles = [
  { id: 7, name: 'Biceps', muscleGroup: 'ARMS' as const },
  { id: 8, name: 'Triceps', muscleGroup: 'ARMS' as const },
  { id: 9, name: 'Lats', muscleGroup: 'BACK' as const },
];

describe('useAnalyticsFilters', () => {
  it('filters muscles and clears a selected muscle when the group changes', () => {
    const { result } = renderHook(() => useAnalyticsFilters(muscles));

    act(() => result.current.changeMuscleGroup('ARMS'));
    expect(result.current.filteredMuscles.map((muscle) => muscle.name))
      .toEqual(['Biceps', 'Triceps']);

    act(() => result.current.setMuscleId(7));
    act(() => result.current.changeMuscleGroup('BACK'));

    expect(result.current.muscleId).toBeUndefined();
    expect(result.current.filteredMuscles.map((muscle) => muscle.name)).toEqual(['Lats']);
  });

  it('changes the lookback without going below one', () => {
    const { result } = renderHook(() => useAnalyticsFilters(muscles, 2));

    act(() => result.current.decreaseLookback());
    act(() => result.current.decreaseLookback());
    expect(result.current.lookback).toBe(1);

    act(() => result.current.increaseLookback());
    expect(result.current.lookback).toBe(2);
  });
});
