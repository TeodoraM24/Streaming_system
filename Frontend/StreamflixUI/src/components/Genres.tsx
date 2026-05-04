import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { VideoPlayer } from './VideoPlayer';

export const Genres: React.FC = () => {
  const [genres, setGenres] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedGenre, setSelectedGenre] = useState<number | null>(null);
  const [genreContent, setGenreContent] = useState<any[]>([]);
  const [playingContent, setPlayingContent] = useState<any>(null);

  const loadGenres = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getGenres();
      setGenres(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load genres');
    } finally {
      setLoading(false);
    }
  };

  const loadGenreMovies = async (genreId: number) => {
    setLoading(true);
    setError('');
    try {
      const allContent = await api.getContent();
      const filteredContent = allContent.filter((content: any) => 
        content.genreIds && content.genreIds.includes(genreId)
      );
      setGenreContent(filteredContent);
      setSelectedGenre(genreId);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load content for genre');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGenres();
  }, []);

  return (
    <div>
      <h2>Genres</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '15px', marginBottom: '30px' }}>
        {genres.map((genre) => (
          <button
            key={genre.genreId}
            onClick={() => loadGenreMovies(genre.genreId)}
            style={{
              padding: '15px',
              cursor: 'pointer',
              background: selectedGenre === genre.genreId ? '#007bff' : 'white',
              color: selectedGenre === genre.genreId ? 'white' : 'black',
              border: '1px solid #ddd',
              borderRadius: '8px',
              fontSize: '16px',
            }}
          >
            {genre.genrename}
          </button>
        ))}
      </div>

      {selectedGenre && (
        <div>
          <h3>Content in this Genre</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
            {genreContent.map((item, index) => (
              <div key={item.contentId || item.movieId || item.showId || index} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', background: 'white', position: 'relative' }}>
                <h4 style={{ marginTop: 0 }}>{item.title || item.originaltitle || 'Untitled'}</h4>
                {item.rating !== undefined && <p><strong>Rating:</strong> {item.rating}/10</p>}
                {item.duration && <p><strong>Duration:</strong> {item.duration} min</p>}
                {item.description && <p style={{ fontSize: '14px', color: '#666' }}>{item.description}</p>}
                <button
                  onClick={() => setPlayingContent(item)}
                  style={{
                    width: '100%',
                    padding: '10px',
                    marginTop: '10px',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '16px',
                    fontWeight: 'bold',
                  }}
                >
                  ▶️ Play
                </button>
              </div>
            ))}
          </div>
          {genreContent.length === 0 && !loading && (
            <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
              No content found for this genre
            </div>
          )}
        </div>
      )}

      {playingContent && (
        <VideoPlayer
          title={playingContent.title || playingContent.originaltitle || 'Untitled'}
          type={playingContent.type === 'MOVIE' ? 'movie' : 'show'}
          duration={playingContent.duration}
          onClose={() => setPlayingContent(null)}
        />
      )}
    </div>
  );
};
