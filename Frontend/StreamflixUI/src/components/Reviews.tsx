import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const Reviews: React.FC = () => {
  const [reviews, setReviews] = useState<any[]>([]);
  const [contentMap, setContentMap] = useState<Record<number, string>>({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [reviewData, contentData] = await Promise.all([
        api.getReviews(),
        api.getContent(),
      ]);

      const map: Record<number, string> = {};
      contentData.forEach((c: any) => {
        map[c.contentId] = c.title || c.originaltitle || `Content #${c.contentId}`;
      });
      setContentMap(map);

      const sorted = reviewData.sort((a: any, b: any) => {
        if (a.createdAt && b.createdAt)
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        return (b.reviewId ?? 0) - (a.reviewId ?? 0);
      });
      setReviews(sorted);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load reviews');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const ratingBadgeColor = (r: number) => r >= 7 ? 'var(--success)' : r >= 4 ? 'var(--warning)' : 'var(--danger)';

  return (
    <div>
      <div className="sf-page-header">
        <h2 className="sf-page-title">⭐ Reviews</h2>
        <span style={{ color: 'var(--text-muted)', fontSize: '13px' }}>
          Go to Movies or Shows and click "Review" to write one
        </span>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}
      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      <div style={{ display: 'grid', gap: '12px' }}>
        {reviews.map((review) => (
          <div key={review.reviewId} className="sf-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '12px' }}>
              <div style={{ minWidth: 0 }}>
                <h3 style={{ fontSize: '15px', fontWeight: '600', color: 'var(--text)', marginBottom: '6px' }}>
                  {review.title}
                </h3>
                {review.contentId && (
                  <span style={{
                    display: 'inline-block', fontSize: '12px', color: 'var(--accent-light)',
                    background: 'rgba(99,102,241,0.12)', padding: '2px 10px', borderRadius: '12px',
                  }}>
                    🎬 {contentMap[review.contentId] || `Content #${review.contentId}`}
                  </span>
                )}
              </div>
              <span style={{
                background: ratingBadgeColor(review.rating), color: 'white', flexShrink: 0,
                padding: '4px 12px', borderRadius: '8px', fontSize: '13px', fontWeight: '700',
              }}>
                ⭐ {review.rating}/10
              </span>
            </div>
            <p style={{ color: 'var(--text)', margin: '12px 0 8px 0', fontSize: '14px', lineHeight: '1.6' }}>
              {review.comment}
            </p>
            {review.createdAt && (
              <span style={{ color: 'var(--text-dim)', fontSize: '12px' }}>
                {new Date(review.createdAt).toLocaleDateString()}
              </span>
            )}
          </div>
        ))}
      </div>

      {!loading && reviews.length === 0 && (
        <div className="sf-empty">⭐ No reviews yet. Go to Movies or Shows to write one!</div>
      )}
    </div>
  );
};
