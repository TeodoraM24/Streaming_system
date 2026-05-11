import { test, expect, Page } from '@playwright/test';

// ─── Shared test user ────────────────────────────────────────────────────────
const TEST_USER = {
  firstname:   'Test',
  lastname:    'Test',
  username:    'TestTest',
  mail:        'testtesttest@gmail.com',
  phonenumber: '42333333',
  password:    'Test123!',
};

const GENRES_TO_TEST = ['Action', 'Comedy', 'Drama'];

// ─── Helpers ─────────────────────────────────────────────────────────────────

async function go_to_register(page: Page) {
  await page.getByRole('button', { name: /register/i }).click();
  await expect(page.getByText(/create your account/i)).toBeVisible();
}

async function register_user(page: Page, user = TEST_USER) {
  await page.locator('input[name="firstname"]').fill(user.firstname);
  await page.locator('input[name="lastname"]').fill(user.lastname);
  await page.locator('input[name="username"]').fill(user.username);
  await page.locator('input[name="mail"]').fill(user.mail);
  await page.locator('input[name="phonenumber"]').fill(user.phonenumber);
  await page.locator('input[name="password"]').fill(user.password);
  await page.getByRole('button', { name: /create account/i }).click();
}

async function login_user(page: Page, username: string, password: string) {
  await page.getByPlaceholder('Enter your username').fill(username);
  await page.getByPlaceholder('Enter your password').fill(password);
  await page.getByRole('button', { name: /sign in/i }).click();
}

async function expect_dashboard(page: Page) {
  await expect(page.locator('button.sf-btn-ghost', { hasText: /logout/i })).toBeVisible({ timeout: 30_000 });
  await expect(page.locator('.sf-nav-item').first()).toBeVisible();
}

async function go_to_genres(page: Page) {
  await page.locator('.sf-nav-item', { hasText: /genres/i }).click();
  await expect(page.getByRole('heading', { name: /genres/i })).toBeVisible({ timeout: 10_000 });
}

async function select_movies_filter(page: Page) {
  await page.locator('.sf-btn-filter', { hasText: /movies/i }).click();
}

async function expect_content_cards(page: Page) {
  await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 30_000 });
  const cards = page.getByRole('main').locator('.sf-card');
  const empty = page.getByRole('main').getByText(/select a genre|no .* found/i);
  await expect(cards.or(empty).first()).toBeVisible({ timeout: 15_000 });
}

// ─── Tests ────────────────────────────────────────────────────────────────────

test.describe('streamflix_full_flow', () => {

  // ── 1. Registration ────────────────────────────────────────────────────────

  test.describe('registration', () => {

    test('shows_register_form_after_clicking_register_link', async ({ page }) => {
      await page.goto('/');
      await expect(page.getByText('Sign in to your account')).toBeVisible();
      await go_to_register(page);
      await expect(page.locator('input[name="firstname"]')).toBeVisible();
      await expect(page.locator('input[name="lastname"]')).toBeVisible();
      await expect(page.locator('input[name="username"]')).toBeVisible();
      await expect(page.locator('input[name="mail"]')).toBeVisible();
      await expect(page.locator('input[name="phonenumber"]')).toBeVisible();
      await expect(page.locator('input[name="password"]')).toBeVisible();
    });

    test('registers_new_user_and_lands_on_dashboard', async ({ page }) => {
      await page.goto('/');
      await go_to_register(page);
      await register_user(page);
      await expect_dashboard(page);
    });

  });

  // ── 2. Login ───────────────────────────────────────────────────────────────

  test.describe('login', () => {

    test('shows_login_form_on_initial_load', async ({ page }) => {
      await page.goto('/');
      await expect(page.getByText('Sign in to your account')).toBeVisible();
      await expect(page.getByPlaceholder('Enter your username')).toBeVisible();
      await expect(page.getByPlaceholder('Enter your password')).toBeVisible();
    });

    test('logs_in_with_valid_credentials_and_shows_dashboard', async ({ page }) => {
      await page.goto('/');
      await login_user(page, TEST_USER.username, TEST_USER.password);
      await expect_dashboard(page);
    });

  });

  // ── 3. Genre browsing ─────────────────────────────────────────────────────

  test.describe('genre_browsing', () => {

    test.beforeEach(async ({ page }) => {
      await page.goto('/');
      await login_user(page, TEST_USER.username, TEST_USER.password);
      await expect_dashboard(page);
    });

    test('genres_tab_loads_genre_pills', async ({ page }) => {
      await go_to_genres(page);
      await expect(page.locator('.sf-genre-pill').first()).toBeVisible({ timeout: 10_000 });
    });

    test('movies_filter_is_selectable_in_genres_tab', async ({ page }) => {
      await go_to_genres(page);
      await select_movies_filter(page);
      await expect(page.locator('.sf-btn-filter', { hasText: /movies/i })).toHaveClass(/active/);
    });

    for (const genre of GENRES_TO_TEST) {
      test(`selecting_genre_${genre.toLowerCase()}_shows_movie_results`, async ({ page }) => {
        await go_to_genres(page);
        await select_movies_filter(page);

        const pill = page.locator('.sf-genre-pill', { hasText: new RegExp(`^${genre}$`, 'i') });
        if (await pill.count() === 0) { test.skip(); return; }

        await pill.click();
        await expect(pill).toHaveClass(/active/);
        await expect_content_cards(page);
      });
    }

    test('switching_between_two_genres_updates_results', async ({ page }) => {
      await go_to_genres(page);
      await select_movies_filter(page);

      const pills = page.locator('.sf-genre-pill');
      await expect(pills.first()).toBeVisible({ timeout: 10_000 });
      if (await pills.count() < 2) { test.skip(); return; }

      await pills.nth(0).click();
      await expect_content_cards(page);

      await pills.nth(1).click();
      await expect_content_cards(page);
      await expect(pills.nth(1)).toHaveClass(/active/);
    });

    test('each_movie_card_shows_title_and_play_button', async ({ page }) => {
      await go_to_genres(page);
      await select_movies_filter(page);

      await page.locator('.sf-genre-pill').first().click();
      await expect_content_cards(page);

      const cards = page.getByRole('main').locator('.sf-card');
      if (await cards.count() === 0) return;

      const card = cards.first();
      const titleText = (await card.locator('h4').textContent())?.trim() ?? '';
      expect(titleText.length).toBeGreaterThan(0);
      await expect(card.getByRole('button', { name: /play/i })).toBeVisible();
    });

  });

  // ── 4. Logout ─────────────────────────────────────────────────────────────

  test.describe('logout', () => {

    test('logout_returns_user_to_login_screen', async ({ page }) => {
      await page.goto('/');
      await login_user(page, TEST_USER.username, TEST_USER.password);
      await expect_dashboard(page);
      await page.locator('button.sf-btn-ghost', { hasText: /logout/i }).click();
      await expect(page.getByText('Sign in to your account')).toBeVisible();
      await expect(page.getByPlaceholder('Enter your username')).toBeVisible();
    });

  });

  // ── 5. Full happy path flow ────────────────────────────────────────────────

  test('full_flow_login_browse_genres_logout', async ({ page }) => {
    // 1. Start on login
    await page.goto('/');
    await expect(page.getByText('Sign in to your account')).toBeVisible();

    // 2. Login with existing user
    await login_user(page, TEST_USER.username, TEST_USER.password);
    await expect_dashboard(page);

    // 3. Go to genres, filter by movies
    await go_to_genres(page);
    await select_movies_filter(page);
    await expect(page.locator('.sf-btn-filter', { hasText: /movies/i })).toHaveClass(/active/);

    // 4. Browse one genre to verify it works
    const pills = page.locator('.sf-genre-pill');
    await expect(pills.first()).toBeVisible({ timeout: 10_000 });
    await pills.first().click();
    await expect(pills.first()).toHaveClass(/active/);
    await expect_content_cards(page);

    // 5. Logout
    await page.locator('button.sf-btn-ghost', { hasText: /logout/i }).click();
    await expect(page.getByText('Sign in to your account')).toBeVisible();
  });

});