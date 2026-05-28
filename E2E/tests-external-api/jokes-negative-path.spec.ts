import { test, expect } from '../tests/AuthHelper/auth';

test.describe('JokeAPI - Negative Path', () => {

    test('shows error when JokeAPI is unreachable', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route => route.abort());
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-alert-error')).toBeVisible();
        await expect(page.locator('.sf-alert-error')).toContainText('Failed to fetch');
    });

    test('shows error when JokeAPI returns 500', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            route.fulfill({ status: 500 })
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-alert-error')).toContainText('Failed to load joke');
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
    });

    test('generate button is disabled while loading', async ({ page }) => {
        // Delay the response to catch the loading state
        await page.route('**/v2.jokeapi.dev/**', route =>
            new Promise(resolve => setTimeout(() => resolve(route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ error: false, category: 'Programming', type: 'twopart', setup: 'Q', delivery: 'A' }) })), 1000))
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.getByRole('button', { name: 'Loading...' })).toBeDisabled();
    });
});