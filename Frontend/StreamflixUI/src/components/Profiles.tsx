import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

export const Profiles: React.FC = () => {
  const { accountId } = useAuth();
  const [profiles, setProfiles] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [newProfileName, setNewProfileName] = useState('');

  const loadProfiles = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getMyProfiles();
      setProfiles(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load profiles');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!accountId) {
      setError('Account ID not available');
      return;
    }
    setLoading(true);
    setError('');
    try {
      await api.createProfile({ profilename: newProfileName, accountId });
      setNewProfileName('');
      setShowCreateForm(false);
      loadProfiles();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create profile');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteProfile = async (id: number) => {
    if (!confirm('Are you sure you want to delete this profile?')) return;
    setLoading(true);
    setError('');
    try {
      await api.deleteProfile(id);
      loadProfiles();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete profile');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfiles();
  }, []);

  return (
    <div>
      <div className="sf-page-header">
        <h2 className="sf-page-title">👤 My Profiles</h2>
        <button onClick={() => setShowCreateForm(!showCreateForm)}
          className={`sf-btn ${showCreateForm ? 'sf-btn-ghost' : 'sf-btn-primary'}`}>
          {showCreateForm ? '✕ Cancel' : '＋ New Profile'}
        </button>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}

      {showCreateForm && (
        <form onSubmit={handleCreateProfile} style={{ marginBottom: '24px', padding: '24px', background: 'var(--surface-2)', border: '1px solid var(--border)', borderRadius: '14px' }}>
          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">Profile Name</label>
            <input type="text" value={newProfileName}
              onChange={(e) => setNewProfileName(e.target.value)}
              required placeholder="e.g. Kids, Work..." className="sf-input" />
          </div>
          <button type="submit" disabled={loading} className="sf-btn sf-btn-primary">
            {loading ? 'Creating…' : 'Create Profile'}
          </button>
        </form>
      )}

      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: '14px' }}>
        {profiles.map((profile) => (
          <div key={profile.profileId} className="sf-card" style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
            <div style={{ fontSize: '32px', textAlign: 'center' }}>👤</div>
            <h3 style={{ textAlign: 'center', fontSize: '15px', fontWeight: '600', color: 'var(--text)' }}>
              {profile.profilename}
            </h3>
            <button onClick={() => handleDeleteProfile(profile.profileId)}
              className="sf-btn sf-btn-danger" style={{ width: '100%', marginTop: 'auto' }}>
              Delete
            </button>
          </div>
        ))}
      </div>

      {!loading && profiles.length === 0 && (
        <div className="sf-empty">👤 No profiles yet. Create your first one!</div>
      )}
    </div>
  );
};
