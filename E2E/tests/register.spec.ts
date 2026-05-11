import { test, expect } from '@playwright/test';

test.describe('Register', () => {
  test('can register a new account and land on dashboard', async ({ page }) => {
    const uid = Date.now();

    await page.goto('/');
    await page.getByRole('button', { name: 'Register' }).click();

    await page.locator('[name="firstname"]').fill('Test');
    await page.locator('[name="lastname"]').fill('User');
    await page.locator('[name="username"]').fill(`testuser${uid}`);
    await page.locator('[name="mail"]').fill(`testuser${uid}@test.com`);
    await page.locator('[name="phonenumber"]').fill(`2${String(uid % 10000000).padStart(7, '0')}`);
    await page.locator('[name="password"]').fill('Test123!');

    const responsePromise = page.waitForResponse(r => r.url().includes('/auth/register'));
    await page.getByRole('button', { name: 'Create Account' }).click();
    const response = await responsePromise;
    expect(response.ok()).toBeTruthy();

    await expect(page.getByRole('button', { name: 'Logout' })).toBeVisible({ timeout: 20000 });
  });
});
