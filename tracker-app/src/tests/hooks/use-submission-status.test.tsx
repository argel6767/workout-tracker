import { act, renderHook, render, screen } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { SubmissionFeedback } from '../../components/submission-feedback';
import { useSubmissionStatus } from '../../hooks/useSubmissionStatus';

afterEach(() => vi.useRealTimers());

describe('submission feedback', () => {
  it('automatically clears submission status', () => {
    vi.useFakeTimers();
    const { result } = renderHook(() => useSubmissionStatus());

    act(() => result.current.setStatus({ type: 'success', message: 'Saved.' }));
    expect(result.current.status?.message).toBe('Saved.');

    act(() => vi.advanceTimersByTime(3500));
    expect(result.current.status).toBeNull();
  });

  it('uses live-region roles appropriate to success and failure', () => {
    const view = render(<SubmissionFeedback status={{ type: 'success', message: 'Saved.' }} />);
    expect(screen.getByRole('status')).toHaveTextContent('Saved.');

    view.rerender(<SubmissionFeedback status={{ type: 'error', message: 'Failed.' }} />);
    expect(screen.getByRole('alert')).toHaveTextContent('Failed.');
  });
});
