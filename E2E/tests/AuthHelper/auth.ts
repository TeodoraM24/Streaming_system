import { test as base, expect } from '@playwright/test';

export const test = base.extend({
  page: async ({ page }, use) => {
    await page.goto('/');
    await page.getByPlaceholder('Enter your username').fill('TestTest');
    await page.getByPlaceholder('Enter your password').fill('Test123!');
    await page.getByRole('button', { name: 'Sign In' }).click();
    await expect(page.getByRole('button', { name: 'Logout' })).toBeVisible();
    await use(page);
  },
});

export { expect } from '@playwright/test';
