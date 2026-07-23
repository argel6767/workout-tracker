import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { TableContainer } from './table';

const state = vi.hoisted(() => ({ result: { data: undefined as unknown, isLoading: false, isError: false } }));

vi.mock('../hooks/useGetWorkoutsByExercise', () => ({ useGetWorkoutsByExercise: () => state.result }));
vi.mock('./exercise-data', () => ({ ExerciseData: () => <div>exercise selector</div> }));
vi.mock('./card', () => ({ ExerciseAiAnalysisCard: () => <div>exercise analysis</div> }));
vi.mock('./strongest-exercises-table', () => ({ StrongestExercisesTables: () => <div>strongest table</div> }));

beforeEach(() => {
  state.result = { data: undefined, isLoading: false, isError: false };
});

describe('table container', () => {
  it('shows empty, loading, and error states', () => {
    const view = render(<TableContainer />);
    expect(screen.getByText(/No data available/)).toBeInTheDocument();

    state.result = { data: undefined, isLoading: true, isError: false };
    view.rerender(<TableContainer />);
    expect(document.querySelector('.loading')).toBeInTheDocument();

    state.result = { data: undefined, isLoading: false, isError: true };
    view.rerender(<TableContainer />);
    expect(screen.getByText('Error loading exercises')).toBeInTheDocument();
  });

  it('renders primitive, object, array, empty, and missing cell values', () => {
    state.result = {
      isLoading: false,
      isError: false,
      data: [{ workoutDate: '2026-07-22', sets: [{ weight: 135, reps: 8 }], tags: ['push', 'chest'], note: null, metadata: { name: 'Heavy' }, empty: [] }],
    };
    render(<TableContainer />);

    expect(screen.getByRole('columnheader', { name: 'Workout Date' })).toBeInTheDocument();
    expect(screen.getByText('135lbs x 8')).toBeInTheDocument();
    expect(screen.getByText('push, chest')).toBeInTheDocument();
    expect(screen.getByText('Heavy')).toBeInTheDocument();
    expect(screen.getAllByText('Empty')).toHaveLength(2);
    expect(screen.getByText('-')).toBeInTheDocument();
  });

  it('switches to strongest-exercise tables', async () => {
    const user = userEvent.setup();
    render(<TableContainer />);
    await user.click(screen.getByRole('button', { name: 'Strongest Exercises' }));
    expect(screen.getByText('strongest table')).toBeInTheDocument();
    expect(screen.queryByText('exercise selector')).not.toBeInTheDocument();
  });
});
