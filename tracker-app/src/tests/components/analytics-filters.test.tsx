import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { LookbackStepper, MuscleTargetFilters } from '../../components/analytics-filters';

describe('shared analytics filters', () => {
  it('renders accessible muscle selectors and reports changes', async () => {
    const user = userEvent.setup();
    const changeMuscleGroup = vi.fn();
    const setMuscleId = vi.fn();

    render(
      <MuscleTargetFilters
        muscleGroup="CHEST"
        muscleId={undefined}
        filteredMuscles={[{ id: 10, name: 'Pectoral', muscleGroup: 'CHEST' }]}
        musclesLoading={false}
        changeMuscleGroup={changeMuscleGroup}
        setMuscleId={setMuscleId}
        musclePlaceholder="All Chest"
      />,
    );

    await user.selectOptions(screen.getByRole('combobox', { name: 'Muscle Group' }), 'BACK');
    await user.selectOptions(screen.getByRole('combobox', { name: 'Specific Muscle' }), '10');

    expect(changeMuscleGroup).toHaveBeenCalledWith('BACK');
    expect(setMuscleId).toHaveBeenCalledWith(10);
  });

  it('renders an accessible bounded lookback control', async () => {
    const user = userEvent.setup();
    const decrease = vi.fn();
    const increase = vi.fn();
    const { rerender } = render(
      <LookbackStepper label="Weeks Back" value={1} onDecrease={decrease} onIncrease={increase} />,
    );

    expect(screen.getByRole('button', { name: 'Decrease weeks back' })).toBeDisabled();
    await user.click(screen.getByRole('button', { name: 'Increase weeks back' }));
    expect(increase).toHaveBeenCalledOnce();

    rerender(<LookbackStepper label="Weeks Back" value={2} onDecrease={decrease} onIncrease={increase} />);
    await user.click(screen.getByRole('button', { name: 'Decrease weeks back' }));
    expect(decrease).toHaveBeenCalledOnce();
  });
});
