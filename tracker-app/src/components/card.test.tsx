import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { AiAnalysisCard, ExerciseAiAnalysisCard } from './card';

vi.mock('../hooks/useGetAiAnalysisByExercise', () => ({
  useGetAiAnalysisByExercise: () => ({ data: { body: 'Steady progress' }, isLoading: false, isError: false }),
}));

describe('AI analysis card', () => {
  it('renders loading, error, empty, and successful states', () => {
    const { rerender } = render(<AiAnalysisCard analysis={undefined} isLoading isError={false} />);
    expect(document.querySelector('.loading')).toBeInTheDocument();

    rerender(<AiAnalysisCard analysis={undefined} isLoading={false} isError />);
    expect(screen.getByText('Error fetching AI analysis')).toBeInTheDocument();

    rerender(<AiAnalysisCard analysis={undefined} isLoading={false} isError={false} emptyMessage="Nothing yet" />);
    expect(screen.getByText('Nothing yet')).toBeInTheDocument();

    rerender(<AiAnalysisCard analysis={{ body: 'Great work', timestamp: '2026-07-22T12:00:00' }} isLoading={false} isError={false} title="Summary" />);
    expect(screen.getByRole('heading', { name: 'Summary' })).toBeInTheDocument();
    expect(screen.getByText('Great work')).toBeInTheDocument();
  });

  it('connects an exercise to its analysis hook', () => {
    render(<ExerciseAiAnalysisCard exerciseId={7} />);
    expect(screen.getByText('Steady progress')).toBeInTheDocument();
  });
});
