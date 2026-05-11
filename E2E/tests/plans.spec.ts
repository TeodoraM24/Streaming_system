import { test, expect } from './AuthHelper/auth';

test.describe('Plans', () => {
  test('both subscription plans are displayed', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /💎 Plans/i }).click();

    await expect(page.getByRole('heading', { name: /Subscription Plans/i })).toBeVisible();

    await expect(page.getByRole('heading', { name: /standard/i })).toBeVisible({ timeout: 15000 });
    await expect(page.getByRole('heading', { name: /premium/i })).toBeVisible({ timeout: 15000 });
  });
});
