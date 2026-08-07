import { expect, test } from '@playwright/test';

test('creates workout data and displays the recorded workout', async ({ page, request }) => {
  const suffix = Date.now();
  const muscleName = `E2E Pectoral ${suffix}`;
  const exerciseName = `E2E Press ${suffix}`;
  const today = new Date().toLocaleDateString('en-CA');
  let muscleId: number | undefined;
  let exerciseId: number | undefined;
  let workoutId: number | undefined;
  let weightId: number | undefined;
  const deleteCreatedRecord = async (resource: string, id: number) => {
    const response = await request.delete(`http://localhost:8080/v1/${resource}/${id}`);
    expect(response.ok(), `Failed to delete E2E ${resource} record ${id}`).toBeTruthy();
  };

  try {
    await page.goto('/');

    await page.getByRole('button', { name: 'Add a Muscle' }).click();
    await page.getByPlaceholder('Biceps').fill(muscleName);
    const [muscleResponse] = await Promise.all([
      page.waitForResponse((response) => response.request().method() === 'POST' && new URL(response.url()).pathname === '/v1/muscles'),
      page.getByRole('button', { name: 'Add Muscle' }).click(),
    ]);
    muscleId = (await muscleResponse.json()).id;
    await expect(page.getByPlaceholder('Biceps')).toHaveValue('');

    await page.getByRole('button', { name: 'Add an Exercise' }).click();
    await page.getByPlaceholder('Bench Press').fill(exerciseName);
    await page.getByRole('checkbox', { name: muscleName }).check();
    await page.getByRole('combobox', { name: 'Primary Muscle' }).selectOption({ label: muscleName });
    await page.getByRole('combobox', { name: 'Exercise Type' }).selectOption('FREE_WEIGHT');
    const [exerciseResponse] = await Promise.all([
      page.waitForResponse((response) => response.request().method() === 'POST' && new URL(response.url()).pathname === '/v1/exercises'),
      page.getByRole('button', { name: 'Add Exercise' }).click(),
    ]);
    exerciseId = (await exerciseResponse.json()).id;
    await expect(page.getByPlaceholder('Bench Press')).toHaveValue('');

    await page.getByRole('button', { name: 'Add a Workout' }).click();
    await page.getByRole('combobox', { name: 'Exercise' }).selectOption({ label: exerciseName });
    await page.getByRole('button', { name: '+ Add Set' }).click();
    await page.getByRole('spinbutton', { name: 'Weight' }).fill('135');
    await page.getByRole('spinbutton', { name: 'Reps' }).fill('8');
    await page.getByLabel('Add entry date (optional)').fill(today);
    const [workoutResponse] = await Promise.all([
      page.waitForResponse((response) => response.request().method() === 'POST' && new URL(response.url()).pathname === '/v1/workouts'),
      page.getByRole('button', { name: 'Add Workout' }).click(),
    ]);
    workoutId = (await workoutResponse.json()).id;

    await page.getByRole('button', { name: 'Add a Weight' }).click();
    await page.getByRole('spinbutton', { name: 'Weight (lbs)' }).fill('182.5');
    await page.getByLabel('Add entry date (optional)').fill(today);
    const [weightResponse] = await Promise.all([
      page.waitForResponse((response) => response.request().method() === 'POST' && new URL(response.url()).pathname === '/v1/weights'),
      page.getByRole('button', { name: 'Add Weight' }).click(),
    ]);
    weightId = (await weightResponse.json()).id;

    const weights = await request.get('http://localhost:8080/v1/weights/dates?numMonthsBack=1');
    expect(weights.ok()).toBeTruthy();
    expect(await weights.json()).toEqual(expect.arrayContaining([expect.objectContaining({ id: weightId, weight: 182.5, entryDate: today })]));

    await page.getByRole('button', { name: 'See Tables' }).click();
    await page.getByRole('combobox', { name: 'Exercise' }).selectOption({ label: exerciseName });
    await expect(page.getByRole('cell', { name: today })).toBeVisible();
    await expect(page.getByText('135lbs x 8')).toBeVisible();
  } finally {
    if (workoutId !== undefined) await deleteCreatedRecord('workouts', workoutId);
    if (weightId !== undefined) await deleteCreatedRecord('weights', weightId);
    if (exerciseId !== undefined) await deleteCreatedRecord('exercises', exerciseId);
    if (muscleId !== undefined) await deleteCreatedRecord('muscles', muscleId);
  }
});
