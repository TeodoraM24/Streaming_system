import { test, expect } from '@playwright/test';

test.describe('Login and Get Top 10 Movies', () => {
  
  test('should login and retrieve top 10 movies', async ({ page }) => {
    await page.goto('/');
    
    await expect(page.getByPlaceholder('Enter your username')).toBeVisible();
    await expect(page.getByPlaceholder('Enter your password')).toBeVisible();
    
    await page.getByPlaceholder('Enter your username').fill('TestTest');
    await page.getByPlaceholder('Enter your password').fill('Test123!');
    
    await page.getByRole('button', { name: /sign in/i }).click();
    
    await expect(page.getByRole('button', { name: /logout/i })).toBeVisible({ timeout: 10000 });
    
    await page.locator('.sf-nav-item', { hasText: /movies/i }).click();
    
    await expect(page.getByRole('heading', { name: /🎬 Movies/i })).toBeVisible({ timeout: 10000 });
    
    const responsePromise = page.waitForResponse(
      response => response.url().includes('/movies/top-movies') && response.status() === 200,
      { timeout: 15000 }
    );
    
    await page.getByRole('button', { name: /⭐ Top 10 Rated/i }).click();
    
    const response = await responsePromise;
    const movies = await response.json();
    
    expect(Array.isArray(movies)).toBeTruthy();
    expect(movies.length).toBeGreaterThan(0);
    expect(movies.length).toBeLessThanOrEqual(10);
    
    console.log(`Retrieved ${movies.length} top rated movies:`);
    movies.forEach((movie: any, index: number) => {
      console.log(`${index + 1}. ${movie.title || movie.originaltitle || 'Unknown Title'} - Rating: ${movie.rating || 'N/A'}`);
    });
    
    await expect(page.getByText(/Top Rated Movies/i)).toBeVisible();
    
    const movieCards = page.locator('.sf-card');
    await expect(movieCards.first()).toBeVisible({ timeout: 10000 });
    
    const cardCount = await movieCards.count();
    expect(cardCount).toBeGreaterThan(0);
    expect(cardCount).toBeLessThanOrEqual(10);
  });
  
});
