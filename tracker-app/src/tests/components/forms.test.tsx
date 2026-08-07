import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ExerciseForm, FormContainer, MuscleForm, WeightForm, WorkoutForm } from '../../components/forms';

const mocks = vi.hoisted(() => ({
  createExercise: vi.fn(),
  createMuscle: vi.fn(),
  createWeight: vi.fn(),
  createWorkout: vi.fn(),
  clear: vi.fn(),
}));

vi.mock('../../api/exercise', () => ({ createExercise: mocks.createExercise }));
vi.mock('../../api/muscles', () => ({ createMuscle: mocks.createMuscle }));
vi.mock('../../api/weights', () => ({ createWeight: mocks.createWeight }));
vi.mock('../../api/workouts', () => ({ createWorkout: mocks.createWorkout }));
vi.mock('../../api/queryClient', () => ({ queryClient: { clear: mocks.clear } }));
vi.mock('../../hooks/useGetMuscles', () => ({
  useGetMuscles: () => ({ data: [{ id: 7, name: 'Biceps', muscleGroup: 'ARMS' }], isLoading: false, isError: false }),
}));
vi.mock('../../components/exercise-data', () => ({
  ExerciseData: ({ handleExerciseChange }: { handleExerciseChange: (event: React.ChangeEvent<HTMLSelectElement>) => void }) => (
    <select aria-label="Exercise" defaultValue="" onChange={handleExerciseChange}>
      <option value="" disabled>Select</option>
      <option value="5">Curl</option>
    </select>
  ),
}));

beforeEach(() => {
  mocks.createExercise.mockResolvedValue({});
  mocks.createMuscle.mockResolvedValue({});
  mocks.createWeight.mockResolvedValue({});
  mocks.createWorkout.mockResolvedValue({});
});

describe('form navigation', () => {
  it('switches between every data-entry domain', async () => {
    const user = userEvent.setup();
    render(<FormContainer />);

    expect(screen.getByRole('heading', { name: 'Add a Workout' })).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Add a Muscle' }));
    expect(screen.getByRole('heading', { name: 'Add a Muscle' })).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Add an Exercise' }));
    expect(screen.getByRole('heading', { name: 'Add an Exercise' })).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Add a Weight' }));
    expect(screen.getByRole('heading', { name: 'Add a Weight Entry' })).toBeInTheDocument();
  });
});

describe('muscle form', () => {
  it('submits the selected muscle data and clears cached queries', async () => {
    const user = userEvent.setup();
    render(<MuscleForm />);

    await user.type(screen.getByPlaceholderText('Biceps'), 'Deltoid');
    await user.selectOptions(screen.getByRole('combobox'), 'SHOULDERS');
    await user.click(screen.getByRole('button', { name: 'Add Muscle' }));

    await waitFor(() => expect(mocks.createMuscle).toHaveBeenCalledWith({ name: 'Deltoid', muscleGroup: 'SHOULDERS' }));
    expect(mocks.clear).toHaveBeenCalled();
    expect(screen.getByPlaceholderText('Biceps')).toHaveValue('');
  });
});

describe('exercise form', () => {
  it('requires a selected worked muscle and submits the complete exercise', async () => {
    const user = userEvent.setup();
    render(<ExerciseForm />);

    const submit = screen.getByRole('button', { name: 'Add Exercise' });
    expect(submit).toBeDisabled();

    await user.type(screen.getByPlaceholderText('Bench Press'), 'Curl');
    await user.click(screen.getByRole('checkbox', { name: 'Biceps' }));
    await user.selectOptions(screen.getAllByRole('combobox')[1], '7');
    await user.click(submit);

    await waitFor(() => expect(mocks.createExercise).toHaveBeenCalledWith({
      name: 'Curl', description: '', musclesWorked: [7], primaryMuscleId: 7, exerciseType: 'BODYWEIGHT',
    }));
    expect(mocks.clear).toHaveBeenCalled();
  });
});

describe('workout form', () => {
  it('adds and edits a set before submitting a workout', async () => {
    const user = userEvent.setup();
    render(<WorkoutForm />);

    await user.selectOptions(screen.getByLabelText('Exercise'), '5');
    await user.click(screen.getByRole('button', { name: '+ Add Set' }));
    await user.clear(screen.getByRole('spinbutton', { name: 'Weight' }));
    await user.type(screen.getByRole('spinbutton', { name: 'Weight' }), '135');
    await user.clear(screen.getByRole('spinbutton', { name: 'Reps' }));
    await user.type(screen.getByRole('spinbutton', { name: 'Reps' }), '8');
    await user.click(screen.getByRole('button', { name: 'Add Workout' }));

    await waitFor(() => expect(mocks.createWorkout).toHaveBeenCalledWith({
      exerciseId: 5, sets: [{ weight: 135, reps: 8 }], workoutDate: null,
    }));
    expect(mocks.clear).toHaveBeenCalled();
  });

  it('removes a set and converts an empty optional date to null', async () => {
    const user = userEvent.setup();
    render(<WorkoutForm />);

    await user.selectOptions(screen.getByLabelText('Exercise'), '5');
    await user.click(screen.getByRole('button', { name: '+ Add Set' }));
    await user.click(screen.getByRole('button', { name: 'Remove' }));
    await user.click(screen.getByRole('button', { name: 'Add Workout' }));

    await waitFor(() => expect(mocks.createWorkout).toHaveBeenCalledWith({ exerciseId: 5, sets: [], workoutDate: null }));
  });
});

describe('weight form', () => {
  it('submits a weight entry and resets the form', async () => {
    const user = userEvent.setup();
    render(<WeightForm />);

    const input = screen.getByRole('spinbutton', { name: 'Weight (lbs)' });
    await user.clear(input);
    await user.type(input, '182.5');
    await user.click(screen.getByRole('button', { name: 'Add Weight' }));

    await waitFor(() => expect(mocks.createWeight).toHaveBeenCalledWith({ weight: 182.5, entryDate: null }));
    expect(mocks.clear).toHaveBeenCalled();
    expect(input).toHaveValue(0);
  });

  it('preserves entered data and does not clear the cache when submission fails', async () => {
    const user = userEvent.setup();
    const consoleError = vi.spyOn(console, 'error').mockImplementation(() => undefined);
    mocks.createWeight.mockRejectedValueOnce(new Error('unavailable'));
    render(<WeightForm />);

    const input = screen.getByRole('spinbutton', { name: 'Weight (lbs)' });
    await user.clear(input);
    await user.type(input, '190');
    await user.click(screen.getByRole('button', { name: 'Add Weight' }));

    await waitFor(() => expect(consoleError).toHaveBeenCalled());
    expect(input).toHaveValue(190);
    expect(mocks.clear).not.toHaveBeenCalled();
    consoleError.mockRestore();
  });
});
