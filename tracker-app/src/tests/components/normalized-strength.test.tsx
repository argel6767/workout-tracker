import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { NormalizedStrengthAnalytics } from '../../components/normalized-strength';

const state = vi.hoisted(() => ({
  result: { data: undefined as unknown, isLoading: false, isError: false },
  query: vi.fn(),
}));

vi.mock('../../hooks/useGetMuscles', () => ({
  useGetMuscles: () => ({
    data: [
      { id: 7, name: 'Biceps', muscleGroup: 'ARMS' },
      { id: 8, name: 'Triceps', muscleGroup: 'ARMS' },
      { id: 9, name: 'Lats', muscleGroup: 'BACK' },
      { id: 10, name: 'E2E Pectoral', muscleGroup: 'CHEST' },
    ],
    isLoading: false,
  }),
}));
vi.mock('../../hooks/useGetNormalizedStrengthAnalysis', () => ({
  useGetNormalizedStrengthAnalysis: (...args: unknown[]) => {
    state.query(...args);
    return state.result;
  },
}));

beforeEach(() => {
  state.result = { data: undefined, isLoading: false, isError: false };
  state.query.mockClear();
});

describe('normalized strength analytics', () => {
  it('queries general muscle groups and requires a specific arm muscle', async () => {
    const user = userEvent.setup();
    render(<NormalizedStrengthAnalytics />);

    expect(state.query).toHaveBeenLastCalledWith('CHEST', undefined, 5);
    expect(screen.queryByRole('combobox', { name: 'Specific Muscle' })).not.toBeInTheDocument();
    expect(screen.queryByRole('option', { name: 'E2E Pectoral' })).not.toBeInTheDocument();

    await user.selectOptions(screen.getByRole('combobox', { name: 'Muscle Group' }), 'ARMS');
    expect(screen.getByText('Select biceps or triceps to view normalized strength')).toBeInTheDocument();
    expect(state.query).toHaveBeenLastCalledWith('ARMS', undefined, 5);
    expect(screen.getByRole('combobox', { name: 'Specific Muscle' })).toBeInTheDocument();

    await user.selectOptions(screen.getByRole('combobox', { name: 'Specific Muscle' }), '7');
    expect(state.query).toHaveBeenLastCalledWith('ARMS', 7, 5);
  });

  it('renders loading, error, empty, and successful states', () => {
    state.result = { data: undefined, isLoading: true, isError: false };
    const view = render(<NormalizedStrengthAnalytics />);
    expect(document.querySelector('.loading')).toBeInTheDocument();

    state.result = { data: undefined, isLoading: false, isError: true };
    view.rerender(<NormalizedStrengthAnalytics />);
    expect(screen.getByText('Error loading normalized strength analytics')).toBeInTheDocument();

    state.result = {
      data: { muscleId: null, muscleGroup: 'CHEST', targetName: 'CHEST', numWeeksBack: 5, trend: [] },
      isLoading: false,
      isError: false,
    };
    view.rerender(<NormalizedStrengthAnalytics />);
    expect(screen.getByText('No normalized strength data available')).toBeInTheDocument();

    state.result = {
      data: {
        muscleId: null,
        muscleGroup: 'CHEST',
        targetName: 'CHEST',
        numWeeksBack: 5,
        trend: [{ weekStart: '2026-08-03', weekEnd: '2026-08-09', averageStrengthIndex: 112.5, exerciseCount: 3 }],
      },
      isLoading: false,
      isError: false,
    };
    view.rerender(<NormalizedStrengthAnalytics />);
    expect(screen.getByRole('heading', { name: 'Normalized Strength  CHEST' })).toBeInTheDocument();
    expect(screen.getByText('112.5')).toBeInTheDocument();
    expect(screen.getByText('12.5% above baseline')).toBeInTheDocument();
    expect(screen.getByText('3 exercises represented')).toBeInTheDocument();
  });
});
