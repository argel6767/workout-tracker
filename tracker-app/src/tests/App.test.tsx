import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import App from '../App';

vi.mock('../components/forms', () => ({ FormContainer: () => <div>form view</div> }));
vi.mock('../components/analytics', () => ({ AnalyticsContainer: () => <div>analytics view</div> }));
vi.mock('../components/table', () => ({ TableContainer: () => <div>table view</div> }));

describe('App navigation', () => {
  it('switches between data entry, analytics, and table views', async () => {
    const user = userEvent.setup();
    render(<App />);

    expect(screen.getByText('form view')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'See Analytics' }));
    expect(screen.getByText('analytics view')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'See Tables' }));
    expect(screen.getByText('table view')).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Insert New Data' }));
    expect(screen.getByText('form view')).toBeInTheDocument();
  });
});
