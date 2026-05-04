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

  const ratingColor = (rating: number) => {
    if (rating >= 7) return '#28a745';
    if (rating >= 4) return '#fd7e14';
    return '#dc3545';
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>Reviews</h2>
        <span style={{ color: '#666', fontSize: '14px' }}>
          To write a review, go to Movies or Shows and click "Write Review" on a card
        </span>
      </div>

      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div style={{ display: 'grid', gap: '16px' }}>
        {reviews.map((review) => (
          <div key={review.reviewId} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '18px', background: 'white' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
              <div>
                <h3 style={{ margin: '0 0 4px 0' }}>{review.title}</h3>
                {review.contentId && (
                  <span style={{
                    display: 'inline-block', fontSize: '13px', color: '#007bff',
                    background: '#e8f0fe', padding: '2px 10px', borderRadius: '12px',
                  }}>
                    🎬 {contentMap[review.contentId] || `Content #${review.contentId}`}
                  </span>
                )}
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexShrink: 0, marginLeft: '12px' }}>
                <span style={{
                  background: ratingColor(review.rating), color: 'white',
                  padding: '4px 10px', borderRadius: '4px', fontSize: '14px', fontWeight: 'bold',
                }}>
                  ⭐ {review.rating}/10
                </span>
              </div>
            </div>
            <p style={{ color: '#333', margin: '10px 0 6px 0' }}>{review.comment}</p>
            {review.createdAt && (
              <span style={{ color: '#aaa', fontSize: '12px' }}>
                {new Date(review.createdAt).toLocaleDateString()}
              </span>
            )}
          </div>
        ))}
      </div>

      {!loading && reviews.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No reviews yet. Go to Movies or Shows to write one!
        </div>
      )}
    </div>
  );
};
