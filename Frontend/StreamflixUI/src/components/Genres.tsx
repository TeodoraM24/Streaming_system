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

  return (
    <div>
      <div className="sf-page-header">
        <h2 className="sf-page-title">
          🎭 Genres{selectedGenre ? ` — ${selectedGenre.genrename}` : ''}
        </h2>
        <div className="sf-filter-bar">
          <button onClick={() => switchTab('movies')}
            className={`sf-btn sf-btn-filter${activeTab === 'movies' ? ' active' : ''}`}>
            🎬 Movies
          </button>
          <button onClick={() => switchTab('shows')}
            className={`sf-btn sf-btn-filter${activeTab === 'shows' ? ' active' : ''}`}>
            📺 Shows
          </button>
        </div>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: '8px', marginBottom: '28px' }}>
        {genres.map((genre) => (
          <button key={genre.genreId} onClick={() => selectGenre(genre)}
            className={`sf-genre-pill${selectedGenre?.genreId === genre.genreId ? ' active' : ''}`}>
            {genre.genrename}
          </button>
        ))}
      </div>

      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      {selectedGenre && !loading && (
        <>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '16px' }}>
            {content.map((item, index) => (
              <div key={item.contentId || item.movieId || item.showId || index}
                className="sf-card" style={{ display: 'flex', flexDirection: 'column' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '8px', marginBottom: '6px' }}>
                  <h4 style={{ fontSize: '15px', fontWeight: '600', color: 'var(--text)', lineHeight: '1.4' }}>
                    {item.title || item.originaltitle || 'Untitled'}
                  </h4>
                  {item.rating !== undefined && (
                    <span className="sf-rating" style={{ flexShrink: 0 }}>⭐ {Number(item.rating).toFixed(1)}</span>
                  )}
                </div>
                <div style={{ display: 'flex', gap: '12px', marginBottom: '8px', flexWrap: 'wrap' }}>
                  {item.duration && <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>🕐 {item.duration} min</span>}
                  {item.releasedate && <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>📅 {item.releasedate}</span>}
                </div>
                {item.description && (
                  <p style={{ fontSize: '13px', color: 'var(--text-muted)', lineHeight: '1.5', flex: 1, marginBottom: '14px',
                    display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                    {item.description}
                  </p>
                )}
                <button onClick={() => setPlayingContent(item)} className="sf-btn sf-btn-primary"
                  style={{ width: '100%', marginTop: 'auto' }}>
                  ▶ Play
                </button>
              </div>
            ))}
          </div>
          {content.length === 0 && (
            <div className="sf-empty">
              No {activeTab} found for <strong>{selectedGenre.genrename}</strong>
            </div>
          )}
        </>
      )}

      {!selectedGenre && !loading && (
        <div className="sf-empty">👆 Select a genre above to browse {activeTab}</div>
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
