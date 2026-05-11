import { test, expect } from './AuthHelper/auth';

test.describe('Movies', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible({ timeout: 30000 });
  });

  test('movies page loads and displays movie cards', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /Movies/i })).toBeVisible();
    await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible();
  });

  test('top 10 rated filter shows top rated movies', async ({ page }) => {
    await page.getByRole('button', { name: '⭐ Top 10 Rated' }).click();

    await expect(page.getByRole('heading', { name: /Top Rated Movies/i })).toBeVisible();
    await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible();
  });

  test('all movies filter resets back to full list', async ({ page }) => {
    await page.getByRole('button', { name: '⭐ Top 10 Rated' }).click();
    await expect(page.getByRole('heading', { name: /Top Rated Movies/i })).toBeVisible();

    await page.getByRole('button', { name: 'All Movies' }).click();
    await expect(page.getByRole('heading', { name: /^🎬 Movies$/i })).toBeVisible();
  });
});
