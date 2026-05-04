import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { VideoPlayer } from './VideoPlayer';

type ContentTab = 'movies' | 'shows';

export const Genres: React.FC = () => {
  const [genres, setGenres] = useState<any[]>([]);
  const [selectedGenre, setSelectedGenre] = useState<any>(null);
  const [activeTab, setActiveTab] = useState<ContentTab>('movies');
  const [content, setContent] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [playingContent, setPlayingContent] = useState<any>(null);

  useEffect(() => {
    api.getGenres()
      .then(setGenres)
      .catch(err => setError(err instanceof Error ? err.message : 'Failed to load genres'));
  }, []);

  const loadContent = async (genreId: number, tab: ContentTab) => {
    setLoading(true);
    setError('');
    try {
      const type = tab === 'movies' ? 'MOVIE' : 'SHOW';
      const all = await api.getContent();
      const filtered = all.filter((c: any) =>
        c.type === type && Array.isArray(c.genreIds) && c.genreIds.includes(genreId)
      );
      setContent(filtered);
    } catch (err) {
      setError(err instanceof Error ? err.message : `Failed to load ${tab}`);
    } finally {
      setLoading(false);
    }
  };

  const selectGenre = (genre: any) => {
    setSelectedGenre(genre);
    loadContent(genre.genreId, activeTab);
  };

  const switchTab = (tab: ContentTab) => {
    setActiveTab(tab);
    if (selectedGenre) loadContent(selectedGenre.genreId, tab);
  };

  const tabBtn = (tab: ContentTab): React.CSSProperties => ({
    padding: '8px 20px',
    cursor: 'pointer',
    border: '1px solid #ddd',
    borderRadius: '4px',
    background: activeTab === tab ? '#007bff' : 'white',
    color: activeTab === tab ? 'white' : '#333',
    fontWeight: activeTab === tab ? 'bold' : 'normal',
  });

  return (
    <div>
      {/* ── Header row: title left, tab buttons right ── */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0 }}>
          Genres{selectedGenre ? ` — ${selectedGenre.genrename}` : ''}
        </h2>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button onClick={() => switchTab('movies')} style={tabBtn('movies')}>
            🎬 Movies
          </button>
          <button onClick={() => switchTab('shows')} style={tabBtn('shows')}>
            📺 Shows
          </button>
        </div>
      </div>

      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

      {/* ── Genre filter grid ── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))', gap: '10px', marginBottom: '28px' }}>
        {genres.map((genre) => (
          <button
            key={genre.genreId}
            onClick={() => selectGenre(genre)}
            style={{
              padding: '12px',
              cursor: 'pointer',
              background: selectedGenre?.genreId === genre.genreId ? '#007bff' : 'white',
              color: selectedGenre?.genreId === genre.genreId ? 'white' : '#333',
              border: '1px solid #ddd',
              borderRadius: '8px',
              fontSize: '14px',
              fontWeight: selectedGenre?.genreId === genre.genreId ? 'bold' : 'normal',
            }}
          >
            {genre.genrename}
          </button>
        ))}
      </div>

      {/* ── Content grid ── */}
      {loading && <div>Loading...</div>}

      {selectedGenre && !loading && (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: '20px' }}>
            {content.map((item, index) => (
              <div key={item.contentId || item.movieId || item.showId || index}
                style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', background: 'white' }}>
                <h4 style={{ marginTop: 0 }}>{item.title || item.originaltitle || 'Untitled'}</h4>
                {item.rating !== undefined && <p style={{ margin: '4px 0' }}><strong>Rating:</strong> {item.rating}/10</p>}
                {item.duration && <p style={{ margin: '4px 0' }}><strong>Duration:</strong> {item.duration} min</p>}
                {item.releasedate && <p style={{ margin: '4px 0' }}><strong>Release:</strong> {item.releasedate}</p>}
                {item.description && <p style={{ fontSize: '13px', color: '#666', margin: '8px 0' }}>{item.description}</p>}
                <button
                  onClick={() => setPlayingContent(item)}
                  style={{ width: '100%', padding: '10px', marginTop: '8px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '15px', fontWeight: 'bold' }}
                >
                  ▶️ Play
                </button>
              </div>
            ))}
          </div>
          {content.length === 0 && (
            <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
              No {activeTab} found for {selectedGenre.genrename}
            </div>
          )}
        </>
      )}

      {playingContent && (
        <VideoPlayer
          title={playingContent.title || playingContent.originaltitle || 'Untitled'}
          type={activeTab === 'movies' ? 'movie' : 'show'}
          duration={playingContent.duration}
          onClose={() => setPlayingContent(null)}
        />
      )}
    </div>
  );
};
