import { test, expect, Page } from '@playwright/test';

const TEST_USER = {
  username: 'TestTest',
  password: 'Test123!',
};

const REVIEW_RATING = '5';
const API_BASE_URL = 'http://localhost:8081/api';

function unique_review_data() {
  const suffix = `${Date.now()}-${Math.floor(Math.random() * 100000)}`;

  return {
    title: `Playwright review ${suffix}`,
    comment: `This review was created by the Playwright review test at ${suffix}.`,
    rating: REVIEW_RATING,
  };
}

async function login_user(page: Page) {
  // Open the app on the login page.
  await page.goto('/');

  // Fill in the existing test user credentials.
  await page.getByPlaceholder('Enter your username').fill(TEST_USER.username);
  await page.getByPlaceholder('Enter your password').fill(TEST_USER.password);

  // Submit the login form and wait until the dashboard is visible.
  await page.getByRole('button', { name: /sign in/i }).click();
  await expect(page.getByRole('button', { name: /logout/i })).toBeVisible({ timeout: 10_000 });
}

async function go_to_movies(page: Page) {
  // Navigate to the Movies page, where reviews can be created.
  await page.locator('.sf-nav-item', { hasText: /movies/i }).click();

  // Wait until the movie cards have loaded.
  await expect(page.locator('.sf-page-title', { hasText: /movies/i })).toBeVisible();
  await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 15_000 });
  await expect(page.getByTestId('movie-card').first()).toBeVisible({ timeout: 15_000 });
}

async function go_to_reviews(page: Page) {
  // Navigate to the Reviews page.
  await page.locator('.sf-nav-item', { hasText: /reviews/i }).click();

  // Wait until the reviews list has loaded.
  await expect(page.locator('.sf-page-title', { hasText: /reviews/i })).toBeVisible();
  await expect(page.getByText(/loading/i)).not.toBeVisible({ timeout: 15_000 });
}

async function get_reviewed_content_ids_for_test_profile(page: Page) {
  // Read existing reviews so the test does not choose content already reviewed by the test profile.
  return page.evaluate(async (apiBaseUrl) => {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };

    const [profilesResponse, reviewsResponse] = await Promise.all([
      fetch(`${apiBaseUrl}/profiles/me`, { headers }),
      fetch(`${apiBaseUrl}/reviews`, { headers }),
    ]);

    const profiles = await profilesResponse.json();
    const reviews = await reviewsResponse.json();
    const testProfile = profiles.find((profile: any) => profile.profilename === 'test');

    if (!testProfile) {
      throw new Error('Could not find profile named "test" for the logged-in account.');
    }

    return reviews
      .filter((review: any) => review.profileId === testProfile.profileId)
      .map((review: any) => review.contentId);
  }, API_BASE_URL);
}

async function find_unreviewed_movie_card(page: Page) {
  // Find a movie that has not already been reviewed by the profile named "test".
  const reviewed_content_ids = new Set(await get_reviewed_content_ids_for_test_profile(page));
  const movie_cards = page.getByTestId('movie-card');
  const movie_count = await movie_cards.count();

  for (let index = 0; index < movie_count; index++) {
    const movie_card = movie_cards.nth(index);
    const content_id_text = await movie_card.getByTestId('movie-card-content-id').textContent();
    const content_id = Number(content_id_text?.trim());

    if (Number.isFinite(content_id) && !reviewed_content_ids.has(content_id)) {
      return movie_card;
    }
  }

  throw new Error('The profile named "test" has already reviewed every visible movie.');
}

async function create_review_for_available_movie(page: Page, review: ReturnType<typeof unique_review_data>) {
  // Open the review modal for a movie the test profile has not reviewed before.
  const movie_card = await find_unreviewed_movie_card(page);
  await expect(movie_card).toBeVisible({ timeout: 15_000 });

  const movie_title = (await movie_card.getByTestId('movie-card-title').textContent())?.trim() ?? '';
  expect(movie_title.length).toBeGreaterThan(0);

  await movie_card.getByTestId('movie-card-review').click();
  await expect(page.getByTestId('review-modal')).toBeVisible();

  // Select the known test profile for the logged-in account.
  const profile_select = page.getByTestId('review-profile-select');
  await expect
    .poll(async () => profile_select.locator('option').count(), { timeout: 15_000 })
    .toBeGreaterThan(1);
  await profile_select.selectOption({ label: 'test' });

  // Fill in all required review fields.
  await page.getByTestId('review-title-input').fill(review.title);
  await page.getByTestId('review-rating-input').fill(review.rating);
  await page.getByTestId('review-comment-input').fill(review.comment);

  // Submit the review and wait for the modal to close.
  await page.getByTestId('review-submit').click();
  await expect(page.getByTestId('review-modal')).not.toBeVisible({ timeout: 15_000 });

  return movie_title;
}

test.describe('reviews', () => {
  test.describe.configure({ mode: 'serial' });

  test.beforeEach(async ({ page }) => {
    // Start every review test as a logged-in user on the Movies page.
    await login_user(page);
    await go_to_movies(page);
  });

    test('creates_a_review_when_required_info_is_entered', async ({ page }) => {
        // Prepare unique review data so this test does not collide with old reviews.
        const review = unique_review_data();

        // Create a review for the first available movie.
        await create_review_for_available_movie(page, review);

        // Verify the user is returned to the movies page after successful submission.
        await expect(page.getByTestId('review-modal')).not.toBeVisible();
        await expect(page.getByTestId('movie-card').first()).toBeVisible();
    });

    test('new_review_is_shown_in_reviews_with_the_right_info', async ({ page }) => {
      // Prepare unique review data so the exact review can be found later.
      const review = unique_review_data();

      // Create a new review and remember which movie it belongs to.
      const movie_title = await create_review_for_available_movie(page, review);

      // Open the Reviews page after the new review has been submitted.
      await go_to_reviews(page);

      // Find the review card by its unique title.
      const review_card = page.getByTestId('review-card').filter({
        has: page.getByTestId('review-title').filter({ hasText: review.title }),
      });
      await expect(review_card).toBeVisible({ timeout: 15_000 });

      // Verify the review card contains the expected title, content, rating, and comment.
      await expect(review_card.getByTestId('review-title')).toHaveText(review.title);
      await expect(review_card.getByTestId('review-content')).toContainText(movie_title);
      await expect(review_card.getByTestId('review-rating')).toContainText(`${review.rating}/10`);
      await expect(review_card.getByTestId('review-comment')).toHaveText(review.comment);
    });
});
