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
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>Movies</h2>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => { setShowTopRated(false); loadMovies(); }} style={{ padding: '8px 16px', cursor: 'pointer' }}>
            All Movies
          </button>
          <button onClick={loadTopMovies} style={{ padding: '8px 16px', cursor: 'pointer' }}>
            Top Rated
          </button>
        </div>
      </div>

      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
        {displayMovies.map((movie) => (
          <div key={movie.contentId || movie.movieId} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', background: 'white', position: 'relative' }}>
            <h3 style={{ marginTop: 0 }}>{movie.title || movie.originaltitle || 'Untitled'}</h3>
            {movie.duration && <p><strong>Duration:</strong> {movie.duration} min</p>}
            {movie.rating !== undefined && <p><strong>Rating:</strong> {movie.rating}/10</p>}
            {movie.releasedate && <p><strong>Release:</strong> {movie.releasedate}</p>}
            {movie.description && <p style={{ fontSize: '14px', color: '#666' }}>{movie.description}</p>}
            <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
              <button
                onClick={() => setPlayingMovie(movie)}
                style={{
                  flex: 1, padding: '10px', backgroundColor: '#007bff',
                  color: 'white', border: 'none', borderRadius: '4px',
                  cursor: 'pointer', fontSize: '15px', fontWeight: 'bold',
                }}
              >
                ▶️ Play
              </button>
              <button
                onClick={() => setReviewingMovie(movie)}
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

      {!loading && displayMovies.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No movies found
        </div>
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
