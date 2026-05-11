import { test, expect } from '@playwright/test';

const TEST_USER = {
  firstname:   'Test',
  lastname:    'Test',
  username:    'TestTest',
  mail:        'testtesttest@gmail.com',
  phonenumber: '42333333',
  password:    'Test123!',
};

const GENRES_TO_TEST = ['Action', 'Comedy', 'Drama'];

test.describe('streamflix_full_flow', () => {

  test('full_flow_register_login_browse_genres_logout', async ({ page }) => {
    // 1. Go to register
    await page.goto('/');
    await expect(page.getByText('Sign in to your account')).toBeVisible();
    await page.getByRole('button', { name: /register/i }).click();
    await expect(page.getByText(/create your account/i)).toBeVisible();

    // 2. Fill in registration form
    await page.locator('input[name="firstname"]').fill(TEST_USER.firstname);
    await page.locator('input[name="lastname"]').fill(TEST_USER.lastname);
    await page.locator('input[name="username"]').fill(TEST_USER.username);
    await page.locator('input[name="mail"]').fill(TEST_USER.mail);
    await page.locator('input[name="phonenumber"]').fill(TEST_USER.phonenumber);
    await page.locator('input[name="password"]').fill(TEST_USER.password);
    await page.getByRole('button', { name: /create account/i }).click();

    // 3. Should land on dashboard
    await expect(page.locator('button', { hasText: /logout/i })).toBeVisible({ timeout: 30_000 });
    await expect(page.locator('.sf-nav-item').first()).toBeVisible();

    // 4. Logout and log back in
    await page.locator('button', { hasText: /logout/i }).click();
    await expect(page.getByText('Sign in to your account')).toBeVisible();
    await page.getByPlaceholder('Enter your username').fill(TEST_USER.username);
    await page.getByPlaceholder('Enter your password').fill(TEST_USER.password);
    await page.getByRole('button', { name: /sign in/i }).click();
    await expect(page.locator('button', { hasText: /logout/i })).toBeVisible({ timeout: 30_000 });

    // 5. Go to genres, filter by movies
    await page.locator('.sf-nav-item', { hasText: /genres/i }).click();
    await expect(page.getByRole('heading', { name: /genres/i })).toBeVisible({ timeout: 10_000 });
    await page.locator('.sf-btn-filter', { hasText: /movies/i }).click();
    await expect(page.locator('.sf-btn-filter', { hasText: /movies/i })).toHaveClass(/active/);

    // 6. Browse each genre
    for (const genre of GENRES_TO_TEST) {
      const pill = page.locator('.sf-genre-pill', { hasText: new RegExp(`^${genre}$`, 'i') });
      if (await pill.count() === 0) continue;
      await pill.click();
      await expect(pill).toHaveClass(/active/);
      await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 60_000 });
      await expect(page.getByRole('main').locator('.sf-card').first()).toBeVisible({ timeout: 60_000 });
    }

    // 7. Logout
    await page.locator('button', { hasText: /logout/i }).click();
    await expect(page.getByText('Sign in to your account')).toBeVisible();
  });

});