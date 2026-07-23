import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { AnalyticsContainer } from './analytics';

vi.mock('./exercise-data', () => ({ ExerciseData: () => <div>exercise filter</div> }));
vi.mock('./linegraphs', () => ({
  ExerciseAnalytics: ({ numMonthsBack }: { numMonthsBack: number }) => <div>general {numMonthsBack}</div>,
  RelativeStrengthAnalytics: () => <div>relative strength view</div>,
  WeightAnalytics: () => <div>weight view</div>,
}));
vi.mock('./piecharts', () => ({
  WorkoutBreakdownPieChart: () => <div>workout breakdown view</div>,
  SetsByMuscleGroupPieChart: () => <div>set breakdown view</div>,
}));
vi.mock('./weekly-volume', () => ({ WeeklyVolumeAnalytics: () => <div>weekly volume view</div> }));
vi.mock('./weekly-one-rep-max', () => ({ WeeklyOneRepMaxAnalytics: () => <div>weekly one rep max view</div> }));

describe('analytics container', () => {
  it('switches among every analytics domain', async () => {
    const user = userEvent.setup();
    render(<AnalyticsContainer />);
    const selector = screen.getByRole('combobox', { name: 'Analytic Type' });

    expect(screen.getByText('general 2')).toBeInTheDocument();

    for (const [value, text] of [
      ['relativeStrength', 'relative strength view'],
      ['weight', 'weight view'],
      ['workoutBreakdown', 'workout breakdown view'],
      ['setBreakdown', 'set breakdown view'],
      ['weeklyVolume', 'weekly volume view'],
      ['weeklyOneRepMax', 'weekly one rep max view'],
    ]) {
      await user.selectOptions(selector, value);
      expect(screen.getByText(text)).toBeInTheDocument();
    }
  });

  it('changes the month range and hides shared filters for weekly views', async () => {
    const user = userEvent.setup();
    render(<AnalyticsContainer />);

    await user.click(screen.getByRole('button', { name: 'Increase months back' }));
    expect(screen.getByText('general 3')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Decrease months back' }));
    expect(screen.getByText('general 2')).toBeInTheDocument();

    await user.selectOptions(screen.getByRole('combobox', { name: 'Analytic Type' }), 'weeklyVolume');
    expect(screen.queryByText('exercise filter')).not.toBeInTheDocument();
    expect(screen.queryByText('Months Back')).not.toBeInTheDocument();
  });
});
