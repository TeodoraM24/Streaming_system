import { test, expect } from './AuthHelper/auth';

test.describe('Movies by Genre', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /Genres/i }).click();
    await expect(page.locator('.sf-genre-pill').first()).toBeVisible({ timeout: 15000 });
  });

  test('genres page loads and displays genre pills', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /Genres/i })).toBeVisible();
    await expect(page.locator('.sf-genre-pill').first()).toBeVisible();
  });

  test('selecting a genre updates the heading and shows movies', async ({ page }) => {
    const firstGenre = page.locator('.sf-genre-pill').first();
    const genreName = (await firstGenre.textContent())!.trim();

    await firstGenre.click();

    await expect(page.getByRole('heading', { name: new RegExp(genreName, 'i') })).toBeVisible();
    await expect(page.locator('.sf-card, .sf-empty').first()).toBeVisible();
  });
});
