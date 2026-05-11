import { test, expect } from './AuthHelper/auth';

test.describe('Profiles', () => {
  test('create a new profile', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /👤 Profiles/i }).click();
    await expect(page.getByRole('heading', { name: /My Profiles/i })).toBeVisible();

    const profileName = `E2EProfile_${Date.now()}`;

    await page.getByRole('button', { name: /New Profile/i }).click();
    await page.getByPlaceholder('e.g. Kids, Work...').fill(profileName);
    await page.getByRole('button', { name: 'Create Profile' }).click();

    await expect(page.getByText(profileName)).toBeVisible({ timeout: 15000 });
  });
});
