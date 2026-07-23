import { render } from '@testing-library/react';
import { expect, it, vi } from 'vitest';
import { axe } from 'vitest-axe';
import { EntryDateForm } from './entry-date';
import { MuscleForm } from './forms';

vi.mock('../api/muscles', () => ({ createMuscle: vi.fn() }));
vi.mock('../api/queryClient', () => ({ queryClient: { clear: vi.fn() } }));

it('has no basic accessibility violations in the shared date input', async () => {
  const { container } = render(<EntryDateForm date={null} onDateChange={() => undefined} />);
  expect((await axe(container)).violations).toEqual([]);
});

it('has no basic accessibility violations in the muscle form', async () => {
  const { container } = render(<MuscleForm />);
  expect((await axe(container)).violations).toEqual([]);
});
