import { test, expect } from '../tests/AuthHelper/auth';

test.describe('JokeAPI - Happy Path', () => {

    test('displays a joke when JokeAPI responds successfully', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({
                    error: false,
                    category: 'Programming',
                    type: 'twopart',
                    setup: 'Why do programmers prefer dark mode?',
                    delivery: 'Because light attracts bugs.'
                })
            })
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-label').filter({ hasText: 'Setup' })).toBeVisible();
        await expect(page.locator('.sf-label').filter({ hasText: 'Delivery' })).toBeVisible();
        await expect(page.getByText('Why do programmers prefer dark mode?')).toBeVisible();
        await expect(page.getByText('Because light attracts bugs.')).toBeVisible();
    });

    test('displays the correct category badge', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({
                    error: false,
                    category: 'Programming',
                    type: 'twopart',
                    setup: 'Why do programmers prefer dark mode?',
                    delivery: 'Because light attracts bugs.'
                })
            })
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();

        await expect(page.locator('.sf-badge-primary').filter({ hasText: 'Programming' })).toBeVisible();
    });

    test('generate button loads a new joke', async ({ page }) => {
        await page.route('**/v2.jokeapi.dev/**', route =>
            route.fulfill({
                status: 200,
                contentType: 'application/json',
                body: JSON.stringify({
                    error: false,
                    category: 'Programming',
                    type: 'twopart',
                    setup: 'Why do programmers prefer dark mode?',
                    delivery: 'Because light attracts bugs.'
                })
            })
        );
        await page.getByRole('button', { name: '😂 Jokes' }).click();
        await expect(page.getByText('Why do programmers prefer dark mode?')).toBeVisible();

        await page.getByRole('button', { name: 'Generate Joke' }).click();

        await expect(page.getByText('Why do programmers prefer dark mode?')).toBeVisible();
    });
});