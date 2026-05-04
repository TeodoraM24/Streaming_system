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

  if (loading && !account) return <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>;

  const row = (label: string, value: string) => (
    <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px 0', borderBottom: '1px solid var(--border)' }}>
      <span style={{ fontSize: '13px', color: 'var(--text-muted)', fontWeight: '600', textTransform: 'uppercase', letterSpacing: '0.7px' }}>{label}</span>
      <span style={{ fontWeight: '600', color: 'var(--text)' }}>{value || '—'}</span>
    </div>
  );

  return (
    <div style={{ maxWidth: '560px' }}>
      <div className="sf-page-header" style={{ marginBottom: '24px' }}>
        <h2 className="sf-page-title">⚙️ My Account</h2>
        {!editing && account && (
          <button onClick={() => setEditing(true)} className="sf-btn sf-btn-ghost">✏️ Edit</button>
        )}
      </div>
      {error && <div className="sf-alert sf-alert-error">{error}</div>}

      {!editing && account && (
        <div style={{ background: 'var(--surface-2)', borderRadius: '14px', border: '1px solid var(--border)', padding: '4px 20px' }}>
          {row('First Name', account.firstname)}
          {row('Last Name', account.lastname)}
          {row('Phone', account.phonenumber)}
          {row('Email', account.mail)}
        </div>
      )}

      {editing && (
        <form onSubmit={handleUpdate} style={{ background: 'var(--surface-2)', padding: '28px', borderRadius: '14px', border: '1px solid var(--border)' }}>
          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">First Name</label>
            <input type="text" value={formData.firstname}
              onChange={(e) => setFormData({ ...formData, firstname: e.target.value })}
              className="sf-input" />
          </div>
          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">Last Name</label>
            <input type="text" value={formData.lastname}
              onChange={(e) => setFormData({ ...formData, lastname: e.target.value })}
              className="sf-input" />
          </div>
          <div style={{ marginBottom: '16px' }}>
            <label className="sf-label">Phone</label>
            <input type="tel" value={formData.phonenumber}
              onChange={(e) => setFormData({ ...formData, phonenumber: e.target.value })}
              className="sf-input" />
          </div>
          <div style={{ marginBottom: '24px' }}>
            <label className="sf-label">Email</label>
            <input type="email" value={formData.mail}
              onChange={(e) => setFormData({ ...formData, mail: e.target.value })}
              className="sf-input" />
          </div>
          <div style={{ display: 'flex', gap: '10px' }}>
            <button type="submit" disabled={loading} className="sf-btn sf-btn-primary">
              {loading ? 'Saving…' : 'Save Changes'}
            </button>
            <button type="button" onClick={() => setEditing(false)} className="sf-btn sf-btn-ghost">Cancel</button>
          </div>
        </form>
      )}
    </div>
  );
};
