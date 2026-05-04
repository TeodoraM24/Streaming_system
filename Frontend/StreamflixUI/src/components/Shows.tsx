import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { VideoPlayer } from './VideoPlayer';
import { ReviewModal } from './ReviewModal';

export const Shows: React.FC = () => {
  const [shows, setShows] = useState<any[]>([]);
  const [topShows, setTopShows] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showTopRated, setShowTopRated] = useState(false);
  const [playingShow, setPlayingShow] = useState<any>(null);
  const [reviewingShow, setReviewingShow] = useState<any>(null);

  const loadShows = async () => {
    setLoading(true);
    setError('');
    try {
      const allContent = await api.getContent();
      const showContent = allContent.filter((content: any) => content.type === 'SHOW');
      setShows(showContent);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load shows');
    } finally {
      setLoading(false);
    }
  };

  const loadTopShows = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getTopShows();
      setTopShows(data);
      setShowTopRated(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load top shows');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadShows();
  }, []);

  const displayShows = showTopRated ? topShows : shows;

  return (
    <div>
      <div className="sf-page-header">
        <h2 className="sf-page-title">
          📺 {showTopRated ? 'Top Rated Shows' : 'TV Shows'}
        </h2>
        <div className="sf-filter-bar">
          <button onClick={() => { setShowTopRated(false); loadShows(); }}
            className={`sf-btn sf-btn-filter${!showTopRated ? ' active' : ''}`}>
            All Shows
          </button>
          <button onClick={loadTopShows}
            className={`sf-btn sf-btn-filter${showTopRated ? ' active' : ''}`}>
            ⭐ Top Rated
          </button>
        </div>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}
      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '16px' }}>
        {displayShows.map((show) => (
          <div key={show.contentId || show.showId} className="sf-card" style={{ display: 'flex', flexDirection: 'column' }}>
            <div style={{ marginBottom: '4px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '8px' }}>
              <h3 style={{ fontSize: '15px', fontWeight: '600', lineHeight: '1.4', color: 'var(--text)' }}>
                {show.title || show.originaltitle || 'Untitled'}
              </h3>
              {show.rating !== undefined && (
                <span className="sf-rating" style={{ flexShrink: 0 }}>⭐ {Number(show.rating).toFixed(1)}</span>
              )}
            </div>
            <div style={{ display: 'flex', gap: '12px', marginBottom: '10px', flexWrap: 'wrap' }}>
              {show.releasedate && <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>📅 {show.releasedate}</span>}
            </div>
            {show.description && (
              <p style={{ fontSize: '13px', color: 'var(--text-muted)', lineHeight: '1.5', flex: 1, marginBottom: '14px',
                display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                {show.description}
              </p>
            )}
            <div style={{ display: 'flex', gap: '8px', marginTop: 'auto' }}>
              <button onClick={() => setPlayingShow(show)} className="sf-btn sf-btn-primary" style={{ flex: 1 }}>
                ▶ Play
              </button>
              <button onClick={() => setReviewingShow(show)} className="sf-btn sf-btn-ghost" style={{ flex: 1 }}>
                ⭐ Review
              </button>
            </div>
          </div>
        ))}
      </div>

      {!loading && displayShows.length === 0 && (
        <div className="sf-empty">📺 No shows found</div>
      )}

      {playingShow && (
        <VideoPlayer
          title={playingShow.title || playingShow.originaltitle || 'Untitled'}
          type="show"
          duration={playingShow.duration}
          onClose={() => setPlayingShow(null)}
        />
      )}

      {reviewingShow && (
        <ReviewModal
          contentId={reviewingShow.contentId}
          contentTitle={reviewingShow.title || reviewingShow.originaltitle || 'Untitled'}
          onClose={() => setReviewingShow(null)}
          onSuccess={() => setReviewingShow(null)}
        />
      )}
    </div>
  );
};
