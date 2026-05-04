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
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>TV Shows</h2>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => { setShowTopRated(false); loadShows(); }} style={{ padding: '8px 16px', cursor: 'pointer' }}>
            All Shows
          </button>
          <button onClick={loadTopShows} style={{ padding: '8px 16px', cursor: 'pointer' }}>
            Top Rated
          </button>
        </div>
      </div>

      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
        {displayShows.map((show) => (
          <div key={show.contentId || show.showId} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', background: 'white', position: 'relative' }}>
            <h3 style={{ marginTop: 0 }}>{show.title || show.originaltitle || 'Untitled'}</h3>
            {show.rating !== undefined && <p><strong>Rating:</strong> {show.rating}/10</p>}
            {show.releasedate && <p><strong>Release:</strong> {show.releasedate}</p>}
            {show.description && <p style={{ fontSize: '14px', color: '#666' }}>{show.description}</p>}
            <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
              <button
                onClick={() => setPlayingShow(show)}
                style={{
                  flex: 1, padding: '10px', backgroundColor: '#007bff',
                  color: 'white', border: 'none', borderRadius: '4px',
                  cursor: 'pointer', fontSize: '15px', fontWeight: 'bold',
                }}
              >
                ▶️ Play
              </button>
              <button
                onClick={() => setReviewingShow(show)}
                style={{
                  flex: 1, padding: '10px', backgroundColor: '#28a745',
                  color: 'white', border: 'none', borderRadius: '4px',
                  cursor: 'pointer', fontSize: '15px', fontWeight: 'bold',
                }}
              >
                ⭐ Review
              </button>
            </div>
          </div>
        ))}
      </div>

      {!loading && displayShows.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No shows found
        </div>
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
