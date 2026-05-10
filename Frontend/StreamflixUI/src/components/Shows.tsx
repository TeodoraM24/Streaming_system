import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { VideoPlayer } from './VideoPlayer';
import { ReviewModal } from './ReviewModal';

interface Episode {
  episodeId: number;
  title?: string;
  description?: string;
  releasedate?: string;
  duration?: number;
}

interface Season {
  seasonId: number;
  title?: string;
  releasedate?: string;
  episodes?: Episode[];
}

export const Shows: React.FC = () => {
  const [shows, setShows] = useState<any[]>([]);
  const [topShows, setTopShows] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showTopRated, setShowTopRated] = useState(false);
  const [selectedShow, setSelectedShow] = useState<any>(null);
  const [showDetails, setShowDetails] = useState<any>(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [expandedSeasonIds, setExpandedSeasonIds] = useState<Set<number>>(new Set());
  const [playingShow, setPlayingShow] = useState<any>(null);
  const [reviewingShow, setReviewingShow] = useState<any>(null);

  const loadShows = async () => {
    setLoading(true);
    setError('');
    try {
      const [allContent, showRows] = await Promise.all([api.getContent(), api.getShows()]);
      const showIdByContentId = new Map(
        showRows.map((show: any) => [show.contentId, show.showsId])
      );
      const showContent = allContent.filter((content: any) => content.type === 'SHOW');
      const sortedShows = showContent.map((content: any) => ({
        ...content,
        showsId: showIdByContentId.get(content.contentId),
      })).sort((a: any, b: any) => {
        const titleA = (a.title || a.originaltitle || '').toLowerCase();
        const titleB = (b.title || b.originaltitle || '').toLowerCase();
        return titleA.localeCompare(titleB);
      });
      setShows(sortedShows);
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

  const openShowDetails = async (show: any) => {
    if (!show.showsId) {
      setError('Could not find the show record for this title.');
      return;
    }

    setSelectedShow(show);
    setShowDetails(null);
    setExpandedSeasonIds(new Set());
    setDetailsLoading(true);
    setError('');
    try {
      const details = await api.getShowById(show.showsId);
      setShowDetails(details);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load show details');
    } finally {
      setDetailsLoading(false);
    }
  };

  useEffect(() => {
    loadShows();
  }, []);

  const displayShows = showTopRated ? topShows : shows;

  const toggleSeason = (seasonId: number) => {
    setExpandedSeasonIds((current) => {
      const next = new Set(current);
      if (next.has(seasonId)) {
        next.delete(seasonId);
      } else {
        next.add(seasonId);
      }
      return next;
    });
  };

  if (selectedShow) {
    const title = selectedShow.title || selectedShow.originaltitle || 'Untitled';
    const seasons: Season[] = showDetails?.seasons || [];
    const episodeCount = seasons.reduce((total, season) => total + (season.episodes?.length || 0), 0);

    return (
      <div data-testid="show-details">
        <div className="sf-page-header" data-testid="show-details-header">
          <div>
            <button
              onClick={() => { setSelectedShow(null); setShowDetails(null); }}
              className="sf-btn sf-btn-ghost"
              style={{ marginBottom: '14px' }}
            >
              ← Back to TV Shows
            </button>
            <h2 className="sf-page-title" data-testid="show-details-title">📺 {title}</h2>
            <div data-testid="show-details-meta" style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginTop: '8px' }}>
              {selectedShow.rating !== undefined && (
                <span className="sf-rating" data-testid="show-details-rating">⭐ {Number(selectedShow.rating).toFixed(1)}</span>
              )}
              {selectedShow.releasedate && (
                <span data-testid="show-details-release-date" style={{ fontSize: '13px', color: 'var(--text-muted)' }}>📅 {selectedShow.releasedate}</span>
              )}
              {!detailsLoading && (
                <span data-testid="show-details-season-summary" style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
                  {seasons.length} seasons · {episodeCount} episodes
                </span>
              )}
            </div>
          </div>
          <button onClick={() => setReviewingShow(selectedShow)} className="sf-btn sf-btn-ghost">
            ⭐ Review
          </button>
        </div>

        {error && <div className="sf-alert sf-alert-error">{error}</div>}

        {selectedShow.description && (
          <div className="sf-card" data-testid="show-details-description" style={{ marginBottom: '18px' }}>
            <p style={{ color: 'var(--text-muted)', lineHeight: '1.6', margin: 0 }}>
              {selectedShow.description}
            </p>
          </div>
        )}

        {detailsLoading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading seasons…</div>}

        {!detailsLoading && seasons.length === 0 && (
          <div className="sf-empty">No seasons or episodes found for this show</div>
        )}

        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {seasons.map((season) => {
            const isExpanded = expandedSeasonIds.has(season.seasonId);
            const seasonEpisodeCount = season.episodes?.length || 0;

            return (
              <section key={season.seasonId} className="sf-card" data-testid="show-season" style={{ padding: 0, overflow: 'hidden' }}>
                <button
                  onClick={() => toggleSeason(season.seasonId)}
                  aria-expanded={isExpanded}
                  data-testid="show-season-toggle"
                  style={{
                    width: '100%',
                    border: 'none',
                    background: 'transparent',
                    cursor: 'pointer',
                    padding: '14px 16px',
                    display: 'flex',
                    justifyContent: 'space-between',
                    gap: '14px',
                    alignItems: 'center',
                    textAlign: 'left',
                  }}
                >
                  <div style={{ minWidth: 0 }}>
                    <h3 style={{ fontSize: '15px', color: 'var(--text)', margin: 0, lineHeight: '1.35' }}>
                      {season.title || `Season ${season.seasonId}`}
                    </h3>
                    <div style={{ display: 'flex', gap: '12px', flexWrap: 'wrap', marginTop: '4px' }}>
                      {season.releasedate && (
                        <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>📅 {season.releasedate}</span>
                      )}
                      <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                        {seasonEpisodeCount} episodes
                      </span>
                    </div>
                  </div>
                  <span
                    style={{
                      color: 'var(--text-muted)',
                      fontSize: '18px',
                      lineHeight: 1,
                      transform: isExpanded ? 'rotate(90deg)' : 'rotate(0deg)',
                      transition: 'transform 0.15s ease',
                      flexShrink: 0,
                    }}
                  >
                    ›
                  </span>
                </button>

                {isExpanded && (
                  <div style={{ borderTop: '1px solid var(--border)', padding: '14px 16px 16px' }}>
                    {seasonEpisodeCount === 0 ? (
                      <div style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
                        No episodes in this season
                      </div>
                    ) : (
                      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: '10px' }}>
                        {(season.episodes || []).map((episode) => (
                          <article
                            key={episode.episodeId}
                            data-testid="show-episode"
                            style={{
                              border: '1px solid var(--border)',
                              borderRadius: '8px',
                              padding: '12px',
                              background: 'var(--surface-alt, rgba(255,255,255,0.02))',
                              display: 'flex',
                              flexDirection: 'column',
                              minHeight: '150px',
                            }}
                          >
                            <div style={{ display: 'flex', justifyContent: 'space-between', gap: '8px', marginBottom: '8px' }}>
                              <h4 style={{ fontSize: '14px', color: 'var(--text)', margin: 0, lineHeight: '1.35' }}>
                                {episode.title || `Episode ${episode.episodeId}`}
                              </h4>
                              {episode.duration && (
                                <span style={{ fontSize: '12px', color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>
                                  🕐 {episode.duration} min
                                </span>
                              )}
                            </div>
                            {episode.description && (
                              <p style={{
                                fontSize: '12px',
                                color: 'var(--text-muted)',
                                lineHeight: '1.45',
                                margin: '0 0 12px',
                                display: '-webkit-box',
                                WebkitLineClamp: 3,
                                WebkitBoxOrient: 'vertical',
                                overflow: 'hidden',
                                flex: 1,
                              }}>
                                {episode.description}
                              </p>
                            )}
                            <button
                              onClick={() => setPlayingShow({ ...selectedShow, episode })}
                              className="sf-btn sf-btn-primary"
                              data-testid="show-episode-play"
                              style={{ marginTop: 'auto' }}
                            >
                              ▶ Play Episode
                            </button>
                          </article>
                        ))}
                      </div>
                    )}
                  </div>
                )}
              </section>
            );
          })}
        </div>

        {playingShow && (
          <VideoPlayer
            title={`${title} - ${playingShow.episode?.title || 'Episode'}`}
            type="show"
            duration={playingShow.episode?.duration}
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
  }

  return (
    <div>
      <div className="sf-page-header">
        <h2 className="sf-page-title">
          📺 {showTopRated ? 'Top Rated Shows' : 'TV Shows'}
        </h2>
        <div className="sf-filter-bar">
          <button onClick={() => { setShowTopRated(false); loadShows(); }}
            data-testid="all-shows-filter"
            className={`sf-btn sf-btn-filter${!showTopRated ? ' active' : ''}`}>
            All Shows
          </button>
          <button onClick={loadTopShows}
            data-testid="top-rated-shows-filter"
            className={`sf-btn sf-btn-filter${showTopRated ? ' active' : ''}`}>
            ⭐ Top 10 Rated
          </button>
        </div>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}
      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '16px' }}>
        {displayShows.map((show) => (
          <div
            key={show.contentId || show.showsId}
            className="sf-card"
            data-testid="show-card"
            onClick={() => openShowDetails(show)}
            style={{ display: 'flex', flexDirection: 'column', cursor: 'pointer' }}
          >
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
              <button onClick={(event) => { event.stopPropagation(); openShowDetails(show); }} className="sf-btn sf-btn-primary" data-testid="show-card-details" style={{ flex: 1 }}>
                ▶ Episodes
              </button>
              <button onClick={(event) => { event.stopPropagation(); setReviewingShow(show); }} className="sf-btn sf-btn-ghost" style={{ flex: 1 }}>
                ⭐ Review
              </button>
            </div>
          </div>
        ))}
      </div>

      {!loading && displayShows.length === 0 && (
        <div className="sf-empty">📺 No shows found</div>
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
