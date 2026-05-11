import { test, expect } from './AuthHelper/auth';

test.describe('Shows by Genre', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /Genres/i }).click();
    await expect(page.locator('.sf-genre-pill').first()).toBeVisible({ timeout: 30000 });
    await page.getByRole('main').getByRole('button', { name: /📺 Shows/i }).click();
  });

  test('selecting a genre shows shows for that genre', async ({ page }) => {
    const firstGenre = page.locator('.sf-genre-pill').first();
    const genreName = (await firstGenre.textContent())!.trim();

    await firstGenre.click();

    await expect(page.getByRole('heading', { name: new RegExp(genreName, 'i') })).toBeVisible({ timeout: 15000 });
    await expect(page.locator('.sf-card, .sf-empty').first()).toBeVisible({ timeout: 30000 });
  });

  test('switching back to movies tab reloads movies for the same genre', async ({ page }) => {
    const firstGenre = page.locator('.sf-genre-pill').first();
    const genreName = (await firstGenre.textContent())!.trim();

    await firstGenre.click();
    await expect(page.getByRole('heading', { name: new RegExp(genreName, 'i') })).toBeVisible({ timeout: 15000 });

    await page.getByRole('main').getByRole('button', { name: /🎬 Movies/i }).click();

    await expect(page.locator('.sf-card, .sf-empty').first()).toBeVisible({ timeout: 30000 });
    await expect(page.getByRole('heading', { name: new RegExp(genreName, 'i') })).toBeVisible();
  });
});
