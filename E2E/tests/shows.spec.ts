import { test, expect, Page, Locator } from '@playwright/test';

const TEST_USER = {
  username: 'TestTest',
  password: 'Test123!',
};

async function login_user(page: Page) {
  await page.goto('/');

  await page.getByPlaceholder('Enter your username').fill(TEST_USER.username);
  await page.getByPlaceholder('Enter your password').fill(TEST_USER.password);

  await page.getByRole('button', { name: /sign in/i }).click();
  await expect(page.getByRole('button', { name: /logout/i })).toBeVisible({ timeout: 10_000 });
}

async function go_to_shows(page: Page) {
  await page.locator('.sf-nav-item', { hasText: /tv shows/i }).click();

  await expect(page.locator('.sf-page-title', { hasText: /tv shows/i })).toBeVisible();
  await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 15_000 });
}

function show_cards(page: Page): Locator {
  return page.getByTestId('show-card');
}

async function open_first_show_details(page: Page) {
  // Find the first show card in the grid.
  const first_show = show_cards(page).first();
  await expect(first_show).toBeVisible({ timeout: 15_000 });

  // Store the card title so the details page can be matched to the selected show.
  const show_title = (await first_show.locator('h3').textContent())?.trim() ?? '';
  expect(show_title.length).toBeGreaterThan(0);

  // Open the details page from the card.
  await first_show.getByTestId('show-card-details').click();

  // Wait until the details page is visible and belongs to the selected show.
  await expect(page.getByRole('button', { name: /back to tv shows/i })).toBeVisible();
  await expect(page.getByTestId('show-details-title')).toContainText(show_title);
  await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 15_000 });

  return show_title;
}

test.describe('show_browsing', () => {
  test.beforeEach(async ({ page }) => {
    // Start every show test as a logged-in user on the TV Shows page.
    await login_user(page);
    await go_to_shows(page);
  });

  test('top_10_rated_shows_list_contains_exactly_10_shows', async ({ page }) => {
    // Switch the list to the Top 10 Rated filter.
    await page.getByTestId('top-rated-shows-filter').click();

    // Wait until the filter finishes loading.
    await expect(page.locator('.sf-page-title', { hasText: /top rated shows/i })).toBeVisible();
    await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 15_000 });

    // Verify that the filtered grid contains exactly ten show cards.
    await expect(show_cards(page)).toHaveCount(10, { timeout: 15_000 });
  });

  test('show_details_contains_all_info', async ({ page }) => {
    // Open the first show from the TV Shows grid.
    await open_first_show_details(page);

    // Verify the main show metadata is present in the details header.
    await expect(page.getByTestId('show-details-rating')).toBeVisible();
    await expect(page.getByTestId('show-details-release-date')).toHaveText(/\d{4}-\d{2}-\d{2}/);
    await expect(page.getByTestId('show-details-season-summary')).toHaveText(/\d+\s+seasons\s+.\s+\d+\s+episodes/i);

    // Verify the description card is present and not empty.
    const description = page.getByTestId('show-details-description').locator('p');
    await expect(description).toBeVisible();
    expect((await description.textContent())?.trim().length ?? 0).toBeGreaterThan(0);

    // Verify at least one season exists before expanding anything.
    const first_season = page.getByTestId('show-season-toggle').first();
    await expect(first_season).toBeVisible();
    await expect(first_season).toHaveAttribute('aria-expanded', 'false');

    // Expand the first season and verify that episodes become visible.
    await first_season.click();
    await expect(first_season).toHaveAttribute('aria-expanded', 'true');
    await expect(page.getByTestId('show-episode').first()).toBeVisible();
    await expect(page.getByTestId('show-episode-play').first()).toBeVisible();
  });

  test('episode_player_opens_and_closes_from_show_details', async ({ page }) => {
    // Open the first show from the TV Shows grid.
    await open_first_show_details(page);

    // Expand the first season so its episodes are shown.
    const first_season = page.getByTestId('show-season-toggle').first();
    await expect(first_season).toBeVisible();
    await first_season.click();

    // Start the first episode and verify that the player opens.
    await page.getByTestId('show-episode-play').first().click();
    await expect(page.getByTestId('video-player')).toBeVisible();

    // Close the player and verify that it disappears.
    await page.getByTestId('video-player-close').click();
    await expect(page.getByTestId('video-player')).not.toBeVisible();
  });
});
