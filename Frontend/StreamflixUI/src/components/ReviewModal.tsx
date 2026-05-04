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
        backgroundColor: 'rgba(0,0,0,0.6)', zIndex: 1000,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: 'white', borderRadius: '10px', padding: '30px',
          width: '100%', maxWidth: '480px', boxShadow: '0 10px 40px rgba(0,0,0,0.3)',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <h2 style={{ marginTop: 0 }}>Write a Review</h2>
        <p style={{ color: '#555', marginBottom: '20px' }}>
          For: <strong>{contentTitle}</strong>
        </p>

        {error && <div style={{ color: 'red', marginBottom: '12px' }}>{error}</div>}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '14px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Profile</label>
            <select
              value={formData.profileId}
              onChange={(e) => setFormData({ ...formData, profileId: e.target.value })}
              required
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #ccc' }}
            >
              <option value="">-- Select a profile --</option>
              {profiles.map((p) => (
                <option key={p.profileId} value={p.profileId}>{p.profilename}</option>
              ))}
            </select>
          </div>

          <div style={{ marginBottom: '14px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Review Title</label>
            <input
              type="text"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
              placeholder="e.g. Amazing film!"
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #ccc' }}
            />
          </div>

          <div style={{ marginBottom: '14px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Rating: {formData.rating}/10</label>
            <input
              type="range"
              min="1"
              max="10"
              value={formData.rating}
              onChange={(e) => setFormData({ ...formData, rating: parseInt(e.target.value) })}
              style={{ width: '100%' }}
            />
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#888' }}>
              <span>1</span><span>5</span><span>10</span>
            </div>
          </div>

          <div style={{ marginBottom: '20px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Comment</label>
            <textarea
              value={formData.comment}
              onChange={(e) => setFormData({ ...formData, comment: e.target.value })}
              required
              rows={4}
              placeholder="Share your thoughts..."
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #ccc', resize: 'vertical' }}
            />
          </div>

          <div style={{ display: 'flex', gap: '10px' }}>
            <button
              type="submit"
              disabled={loading}
              style={{
                flex: 1, padding: '10px', backgroundColor: '#007bff', color: 'white',
                border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', fontSize: '15px',
              }}
            >
              {loading ? 'Submitting...' : '⭐ Submit Review'}
            </button>
            <button
              type="button"
              onClick={onClose}
              style={{
                padding: '10px 20px', backgroundColor: '#f0f0f0', color: '#333',
                border: 'none', borderRadius: '4px', cursor: 'pointer',
              }}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
