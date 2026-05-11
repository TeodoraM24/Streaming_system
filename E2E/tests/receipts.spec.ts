import { test, expect } from './AuthHelper/auth';

test.describe('Receipts', () => {
  test('receipts page displays receipts from subscription payments', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: /🧾 Receipts/i }).click();

    await expect(page.getByRole('heading', { name: /My Receipts/i })).toBeVisible();
    await expect(page.getByText('Loading…')).not.toBeVisible({ timeout: 30000 });

    await expect(page.locator('text=Receipt').first()).toBeVisible();
    await expect(page.getByText('✓ Paid').first()).toBeVisible();
  });
});
