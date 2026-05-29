import { test, expect } from '../tests/AuthHelper/auth';

test.describe('JokeAPI - Negative Path', () => {

    test('shows error when JokeAPI is unreachable', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route => route.abort());
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-alert-error')).toBeVisible();
        await expect(page.locator('.sf-alert-error')).toContainText('Failed to fetch');

        await page.unroute('**/v2.jokeapi.dev/**');

        // ✅ Confirm own API is unaffected
        await page.getByRole('button', { name: '🎬 Movies' }).click();
        await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible();
    });

    test('shows error when JokeAPI returns 500', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            route.fulfill({ status: 500 })
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-alert-error')).toContainText('Failed to load joke');

        await page.unroute('**/v2.jokeapi.dev/**');

        // ✅ Confirm own API is unaffected
        await page.getByRole('button', { name: '🎬 Movies' }).click();
        await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible();
    });

    test('shows error when JokeAPI returns error flag in body', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({ error: true })
            })
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-alert-error')).toContainText('JokeAPI returned an error');

        await page.unroute('**/v2.jokeapi.dev/**');

        // ✅ Confirm own API is unaffected
        await page.getByRole('button', { name: '🎬 Movies' }).click();
        await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible();
    });

    test('generate button is disabled while loading', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            new Promise(resolve => setTimeout(() => resolve(route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({ error: false, category: 'Programming', type: 'twopart', setup: 'Q', delivery: 'A' })
            })), 3000))
        );

        await page.getByRole('button', { name: '😂 Jokes' }).click();

        // Catch loading state immediately after mount
        await expect(page.getByRole('button', { name: 'Loading...' })).toBeDisabled({ timeout: 1000 });

        // Wait for the delayed response to fully complete
        await expect(page.getByRole('button', { name: 'Generate Joke' })).toBeEnabled({ timeout: 10000 });

        await page.unroute('**/v2.jokeapi.dev/**');

        // ✅ Confirm own API is unaffected
        await page.getByRole('button', { name: '🎬 Movies' }).click();
        await expect(page.locator('[data-testid="movie-card"]').first()).toBeVisible();
        console.log('✅ Movies loaded successfully after JokeAPI failure');

    });
});