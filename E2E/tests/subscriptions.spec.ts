import { test, expect } from './AuthHelper/auth';

test.describe('Subscriptions', () => {
  test('create a subscription', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /📋 Subscriptions/i }).click();
    await expect(page.getByRole('heading', { name: /My Subscription/i })).toBeVisible();

    // Wait for loading to finish
    await expect(page.getByText('Loading…')).not.toBeVisible({ timeout: 15000 });

    // If already subscribed, just confirm the active subscription is visible
    const alreadySubscribed = await page.getByText('✓ Active Subscription').isVisible();
    if (alreadySubscribed) {
      await expect(page.getByText('✓ Active Subscription')).toBeVisible();
      return;
    }

    // Step 1: choose a plan
    await expect(page.getByText('Step 1 of 2')).toBeVisible();
    await page.locator('select').selectOption({ index: 1 });
    await page.getByRole('button', { name: /Next: Payment/i }).click();

    // Step 2: fill payment details
    await expect(page.getByText('Step 2 of 2')).toBeVisible();
    await page.getByPlaceholder('1234 5678 9012 3456').fill('4111111111111111');
    await page.getByPlaceholder('MM').fill('12');
    await page.getByPlaceholder('YYYY').fill('2027');
    await page.getByPlaceholder('123', { exact: true }).fill('123');
    await page.getByRole('button', { name: /Pay/i }).click();

    // Verify receipt
    await expect(page.getByText('Payment Successful!')).toBeVisible({ timeout: 15000 });
  });
});
