import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { VideoPlayer } from './VideoPlayer';
import { ReviewModal } from './ReviewModal';

export const Movies: React.FC = () => {
  const [movies, setMovies] = useState<any[]>([]);
  const [topMovies, setTopMovies] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showTopRated, setShowTopRated] = useState(false);
  const [playingMovie, setPlayingMovie] = useState<any>(null);
  const [reviewingMovie, setReviewingMovie] = useState<any>(null);

  const loadMovies = async () => {
    setLoading(true);
    setError('');
    try {
      const allContent = await api.getContent();
      const movieContent = allContent.filter((content: any) => content.type === 'MOVIE');
      setMovies(movieContent);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load movies');
    } finally {
      setLoading(false);
    }
  };

  const loadTopMovies = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getTopMovies();
      setTopMovies(data);
      setShowTopRated(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load top movies');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMovies();
  }, []);

  const displayMovies = showTopRated ? topMovies : movies;

  return (
    <div>
      <div className="sf-page-header">
        <h2 className="sf-page-title">
          🎬 {showTopRated ? 'Top Rated Movies' : 'Movies'}
        </h2>
        <div className="sf-filter-bar">
          <button onClick={() => { setShowTopRated(false); loadMovies(); }}
            className={`sf-btn sf-btn-filter${!showTopRated ? ' active' : ''}`}>
            All Movies
          </button>
          <button onClick={loadTopMovies}
            className={`sf-btn sf-btn-filter${showTopRated ? ' active' : ''}`}>
            ⭐ Top Rated
          </button>
        </div>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}
      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '16px' }}>
        {displayMovies.map((movie) => (
          <div key={movie.contentId || movie.movieId} className="sf-card" style={{ display: 'flex', flexDirection: 'column' }}>
            <div style={{ marginBottom: '4px', display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '8px' }}>
              <h3 style={{ fontSize: '15px', fontWeight: '600', lineHeight: '1.4', color: 'var(--text)' }}>
                {movie.title || movie.originaltitle || 'Untitled'}
              </h3>
              {movie.rating !== undefined && (
                <span className="sf-rating" style={{ flexShrink: 0 }}>⭐ {Number(movie.rating).toFixed(1)}</span>
              )}
            </div>
            <div style={{ display: 'flex', gap: '12px', marginBottom: '10px', flexWrap: 'wrap' }}>
              {movie.duration && <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>🕐 {movie.duration} min</span>}
              {movie.releasedate && <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>📅 {movie.releasedate}</span>}
            </div>
            {movie.description && (
              <p style={{ fontSize: '13px', color: 'var(--text-muted)', lineHeight: '1.5', flex: 1, marginBottom: '14px',
                display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                {movie.description}
              </p>
            )}
            <div style={{ display: 'flex', gap: '8px', marginTop: 'auto' }}>
              <button onClick={() => setPlayingMovie(movie)} className="sf-btn sf-btn-primary" style={{ flex: 1 }}>
                ▶ Play
              </button>
              <button onClick={() => setReviewingMovie(movie)} className="sf-btn sf-btn-ghost" style={{ flex: 1 }}>
                ⭐ Review
              </button>
            </div>
          </div>
        ))}
      </div>

      {!loading && displayMovies.length === 0 && (
        <div className="sf-empty">🎬 No movies found</div>
      )}

      {playingMovie && (
        <VideoPlayer
          title={playingMovie.title || playingMovie.originaltitle || 'Untitled'}
          type="movie"
          duration={playingMovie.duration}
          onClose={() => setPlayingMovie(null)}
        />
      )}

      {reviewingMovie && (
        <ReviewModal
          contentId={reviewingMovie.contentId}
          contentTitle={reviewingMovie.title || reviewingMovie.originaltitle || 'Untitled'}
          onClose={() => setReviewingMovie(null)}
          onSuccess={() => setReviewingMovie(null)}
        />
      )}
    </div>
  );
};
