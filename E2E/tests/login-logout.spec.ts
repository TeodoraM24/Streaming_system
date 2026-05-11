import { test, expect } from '@playwright/test';

test.describe('Authentication', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
    await page.getByPlaceholder('Enter your username').fill('TestTest');
    await page.getByPlaceholder('Enter your password').fill('Test123!');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await expect(page.getByRole('button', { name: 'Logout' })).toBeVisible();
  });

  test('login with valid credentials navigates to dashboard', async ({ page }) => {
    await expect(page.getByText('TestTest')).toBeVisible();
  });

  test('logout returns to login page', async ({ page }) => {
    await page.getByRole('button', { name: 'Logout' }).click();

    await expect(page.getByRole('button', { name: 'Sign In' })).toBeVisible();
    await expect(page.getByText('Sign in to your account')).toBeVisible();
  });
});
