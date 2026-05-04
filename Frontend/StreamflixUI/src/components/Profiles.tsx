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
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>My Profiles</h2>
        <button onClick={() => setShowCreateForm(!showCreateForm)} style={{ padding: '8px 16px', cursor: 'pointer' }}>
          {showCreateForm ? 'Cancel' : 'Create Profile'}
        </button>
      </div>

      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

      {showCreateForm && (
        <form onSubmit={handleCreateProfile} style={{ marginBottom: '20px', padding: '20px', background: 'white', border: '1px solid #ddd', borderRadius: '8px' }}>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>Profile Name:</label>
            <input
              type="text"
              value={newProfileName}
              onChange={(e) => setNewProfileName(e.target.value)}
              required
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
            />
          </div>
          <button type="submit" disabled={loading} style={{ padding: '8px 16px', cursor: 'pointer' }}>
            {loading ? 'Creating...' : 'Create'}
          </button>
        </form>
      )}

      {loading && <div>Loading...</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: '20px' }}>
        {profiles.map((profile) => (
          <div key={profile.profileId} style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '15px', background: 'white' }}>
            <h3 style={{ marginTop: 0 }}>{profile.profilename}</h3>
            <button
              onClick={() => handleDeleteProfile(profile.profileId)}
              style={{ padding: '6px 12px', cursor: 'pointer', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px' }}
            >
              Delete
            </button>
          </div>
        ))}
      </div>

      {!loading && profiles.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No profiles found. Create your first profile!
        </div>
      )}
    </div>
  );
};
