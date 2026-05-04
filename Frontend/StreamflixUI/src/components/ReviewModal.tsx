import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

interface ReviewModalProps {
  contentId: number;
  contentTitle: string;
  onClose: () => void;
  onSuccess: () => void;
}

export const ReviewModal: React.FC<ReviewModalProps> = ({ contentId, contentTitle, onClose, onSuccess }) => {
  const [profiles, setProfiles] = useState<any[]>([]);
  const [formData, setFormData] = useState({
    title: '',
    rating: 5,
    comment: '',
    profileId: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    api.getMyProfiles()
      .then(setProfiles)
      .catch(() => setError('Could not load profiles. Make sure you have at least one profile.'));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await api.createReview({
        title: formData.title,
        rating: formData.rating,
        comment: formData.comment,
        profileId: parseInt(formData.profileId),
        contentId,
      });
      onSuccess();
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to submit review');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
        backgroundColor: 'rgba(0,0,0,0.75)', zIndex: 1000,
        display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px',
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: 'var(--surface)', border: '1px solid var(--border)',
          borderRadius: '16px', padding: '32px',
          width: '100%', maxWidth: '480px', boxShadow: '0 24px 64px rgba(0,0,0,0.6)',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <h2 style={{ marginBottom: '4px', fontSize: '20px', color: 'var(--text)' }}>Write a Review</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: '24px', fontSize: '14px' }}>
          For: <strong style={{ color: 'var(--text)' }}>{contentTitle}</strong>
        </p>

        {error && <div className="sf-alert sf-alert-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">Profile</label>
            <select value={formData.profileId}
              onChange={(e) => setFormData({ ...formData, profileId: e.target.value })}
              required className="sf-input">
              <option value="">— Select a profile —</option>
              {profiles.map((p) => (
                <option key={p.profileId} value={p.profileId}>{p.profilename}</option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">Review Title</label>
            <input type="text" value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required placeholder="e.g. Amazing film!" className="sf-input" />
          </div>

          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">Rating: {formData.rating}/10</label>
            <input type="range" min="1" max="10" value={formData.rating}
              onChange={(e) => setFormData({ ...formData, rating: parseInt(e.target.value) })}
              style={{ width: '100%', accentColor: 'var(--accent)', marginBottom: '4px' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: 'var(--text-dim)' }}>
              <span>1</span><span>5</span><span>10</span>
            </div>
          </div>

          <div style={{ marginBottom: '24px' }}>
            <label className="sf-label">Comment</label>
            <textarea value={formData.comment}
              onChange={(e) => setFormData({ ...formData, comment: e.target.value })}
              required rows={4} placeholder="Share your thoughts..."
              className="sf-input" style={{ resize: 'vertical', height: 'auto' }} />
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <button type="submit" disabled={loading} className="sf-btn sf-btn-primary"
              style={{ flex: 1, padding: '12px', fontSize: '14px' }}>
              {loading ? 'Submitting…' : '⭐ Submit Review'}
            </button>
            <button type="button" onClick={onClose} className="sf-btn sf-btn-ghost">
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
