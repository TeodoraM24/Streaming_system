import { test, expect } from './AuthHelper/auth';

test.describe('Reviews', () => {
  test('create a review for a movie', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible({ timeout: 15000 });

    const movieCount = await page.locator('[data-testid="movie-card-review"]').count();
    await page.locator('[data-testid="movie-card-review"]').nth(Date.now() % movieCount).click();
    await expect(page.locator('[data-testid="review-modal"]')).toBeVisible();

    await expect(page.locator('[data-testid="review-profile-select"] option').nth(1)).toBeAttached({ timeout: 10000 });
    const optionCount = await page.locator('[data-testid="review-profile-select"] option').count();
    await page.locator('[data-testid="review-profile-select"]').selectOption({ index: optionCount - 1 });
    await page.locator('[data-testid="review-title-input"]').fill(`Great movie! ${Date.now()}`);
    await page.locator('[data-testid="review-comment-input"]').fill('This was a fantastic movie, highly recommended!');

    await page.locator('[data-testid="review-submit"]').click();

    await expect(page.locator('[data-testid="review-modal"]')).not.toBeVisible({ timeout: 10000 });
  });

  test('view all reviews', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /⭐ Reviews/i }).click();

    await expect(page.getByRole('heading', { name: /Reviews/i })).toBeVisible();
    await expect(page.locator('[data-testid="review-card"], .sf-empty').first()).toBeVisible();
  });
});
