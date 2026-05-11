import { test, expect } from './AuthHelper/auth';

test.describe('TV Shows', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /TV Shows/i }).click();
    await expect(page.locator('[data-testid="show-card"]').first()).toBeVisible();
  });

  test('shows page loads and displays show cards', async ({ page }) => {
    await expect(page.getByRole('heading', { name: /TV Shows/i })).toBeVisible();
    await expect(page.locator('[data-testid="show-card"]').first()).toBeVisible();
  });

  test('top 10 rated filter shows top rated shows', async ({ page }) => {
    await page.locator('[data-testid="top-rated-shows-filter"]').click();

    await expect(page.getByRole('heading', { name: /Top Rated Shows/i })).toBeVisible();
    await expect(page.locator('[data-testid="show-card"]').first()).toBeVisible();
  });

  test('all shows filter resets back to full list', async ({ page }) => {
    await page.locator('[data-testid="top-rated-shows-filter"]').click();
    await expect(page.getByRole('heading', { name: /Top Rated Shows/i })).toBeVisible();

    await page.locator('[data-testid="all-shows-filter"]').click();
    await expect(page.getByRole('heading', { name: /^📺 TV Shows$/i })).toBeVisible();
  });

  test('clicking episodes button opens show detail view', async ({ page }) => {
    await page.locator('[data-testid="show-card-details"]').first().click();

    await expect(page.locator('[data-testid="show-details"]')).toBeVisible();
    await expect(page.locator('[data-testid="show-details-title"]')).toBeVisible();
    await expect(page.locator('[data-testid="show-details-season-summary"]')).toBeVisible();
  });

  test('back button returns to show list from detail view', async ({ page }) => {
    await page.locator('[data-testid="show-card-details"]').first().click();
    await expect(page.locator('[data-testid="show-details"]')).toBeVisible();

    await page.getByRole('button', { name: /← Back to TV Shows/i }).click();

    await expect(page.locator('[data-testid="show-card"]').first()).toBeVisible();
  });
});
