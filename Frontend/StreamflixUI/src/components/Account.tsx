import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const Account: React.FC = () => {
  const [account, setAccount] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState({
    firstname: '',
    lastname: '',
    phonenumber: '',
    mail: '',
  });

  const loadAccount = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getMyAccount();
      setAccount(data);
      setFormData({
        firstname: data.firstname || '',
        lastname: data.lastname || '',
        phonenumber: data.phonenumber || '',
        mail: data.mail || '',
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load account');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const updated = await api.patchAccount(account.accountId, formData);
      setAccount(updated);
      setEditing(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update account');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAccount();
  }, []);

  if (loading && !account) return <div>Loading...</div>;

  return (
    <div style={{ maxWidth: '600px' }}>
      <h2>My Account</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}

      {!editing && account && (
        <div style={{ background: 'white', padding: '20px', borderRadius: '8px', border: '1px solid #ddd' }}>
          <p><strong>First Name:</strong> {account.firstname}</p>
          <p><strong>Last Name:</strong> {account.lastname}</p>
          <p><strong>Phone:</strong> {account.phonenumber}</p>
          <p><strong>Email:</strong> {account.mail}</p>
          <button onClick={() => setEditing(true)} style={{ padding: '8px 16px', cursor: 'pointer', marginTop: '10px' }}>
            Edit Account
          </button>
        </div>
      )}

      {editing && (
        <form onSubmit={handleUpdate} style={{ background: 'white', padding: '20px', borderRadius: '8px', border: '1px solid #ddd' }}>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>First Name:</label>
            <input
              type="text"
              value={formData.firstname}
              onChange={(e) => setFormData({ ...formData, firstname: e.target.value })}
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>Last Name:</label>
            <input
              type="text"
              value={formData.lastname}
              onChange={(e) => setFormData({ ...formData, lastname: e.target.value })}
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>Phone:</label>
            <input
              type="tel"
              value={formData.phonenumber}
              onChange={(e) => setFormData({ ...formData, phonenumber: e.target.value })}
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px' }}>Email:</label>
            <input
              type="email"
              value={formData.mail}
              onChange={(e) => setFormData({ ...formData, mail: e.target.value })}
              style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
            />
          </div>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button type="submit" disabled={loading} style={{ padding: '8px 16px', cursor: 'pointer' }}>
              {loading ? 'Saving...' : 'Save'}
            </button>
            <button type="button" onClick={() => setEditing(false)} style={{ padding: '8px 16px', cursor: 'pointer' }}>
              Cancel
            </button>
          </div>
        </form>
      )}
    </div>
  );
};
