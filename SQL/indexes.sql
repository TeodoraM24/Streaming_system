-- =========================================================
-- INDEXES
-- =========================================================

-- Episodes are queried by season frequently (e.g. "get all episodes for season X")
CREATE INDEX idx_episode_season_id ON episode(season_season_id);

-- Seasons are queried by show frequently (e.g. "get all seasons for show X")
CREATE INDEX idx_season_shows_id ON season(shows_shows_id);

-- Reviews are queried by content frequently (e.g. "get all reviews for movie X")
CREATE INDEX idx_review_content_id ON review(content_content_id);

-- Subscription status is checked on every login / account lookup
CREATE INDEX idx_subscription_account_id ON subscription(accounts_account_id);

-- Content is frequently browsed/filtered by type (MOVIE or SHOW)
CREATE INDEX idx_content_type ON content(type);
