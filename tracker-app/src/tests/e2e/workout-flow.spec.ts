import { expect, test } from '@playwright/test';

test('creates workout data and displays the recorded workout', async ({ page, request }) => {
  const suffix = Date.now();
  const muscleName = `E2E Pectoral ${suffix}`;
  const exerciseName = `E2E Press ${suffix}`;
  const today = new Date().toLocaleDateString('en-CA');

  await page.goto('/');

  await page.getByRole('button', { name: 'Add a Muscle' }).click();
  await page.getByPlaceholder('Biceps').fill(muscleName);
  await page.getByRole('button', { name: 'Add Muscle' }).click();
  await expect(page.getByPlaceholder('Biceps')).toHaveValue('');

  await page.getByRole('button', { name: 'Add an Exercise' }).click();
  await page.getByPlaceholder('Bench Press').fill(exerciseName);
  await page.getByRole('checkbox', { name: muscleName }).check();
  await page.getByRole('combobox', { name: 'Primary Muscle' }).selectOption({ label: muscleName });
  await page.getByRole('combobox', { name: 'Exercise Type' }).selectOption('FREE_WEIGHT');
  await page.getByRole('button', { name: 'Add Exercise' }).click();
  await expect(page.getByPlaceholder('Bench Press')).toHaveValue('');

  await page.getByRole('button', { name: 'Add a Workout' }).click();
  await page.getByRole('combobox', { name: 'Exercise' }).selectOption({ label: exerciseName });
  await page.getByRole('button', { name: '+ Add Set' }).click();
  await page.getByRole('spinbutton', { name: 'Weight' }).fill('135');
  await page.getByRole('spinbutton', { name: 'Reps' }).fill('8');
  await page.getByLabel('Add entry date (optional)').fill(today);
  await page.getByRole('button', { name: 'Add Workout' }).click();

  await page.getByRole('button', { name: 'Add a Weight' }).click();
  await page.getByRole('spinbutton', { name: 'Weight (lbs)' }).fill('182.5');
  await page.getByLabel('Add entry date (optional)').fill(today);
  await page.getByRole('button', { name: 'Add Weight' }).click();

  const weights = await request.get('http://localhost:8080/v1/weights/dates?numMonthsBack=1');
  expect(weights.ok()).toBeTruthy();
  expect(await weights.json()).toEqual(expect.arrayContaining([expect.objectContaining({ weight: 182.5, entryDate: today })]));

  await page.getByRole('button', { name: 'See Tables' }).click();
  await page.getByRole('combobox', { name: 'Exercise' }).selectOption({ label: exerciseName });
  await expect(page.getByRole('cell', { name: today })).toBeVisible();
  await expect(page.getByText('135lbs x 8')).toBeVisible();
});
