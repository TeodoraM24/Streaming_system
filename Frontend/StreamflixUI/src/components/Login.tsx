import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

export const Login: React.FC<{ onSwitchToRegister: () => void }> = ({ onSwitchToRegister }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login({ username, password });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
      <div style={{ width: '100%', maxWidth: '400px' }}>
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <div style={{ fontSize: '36px', marginBottom: '8px' }}>▶</div>
          <div style={{ fontSize: '26px', fontWeight: '800', background: 'linear-gradient(135deg, #6366f1, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Streamflix
          </div>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px', fontSize: '14px' }}>Sign in to your account</p>
        </div>

        <div style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '16px', padding: '32px' }}>
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '18px' }}>
              <label className="sf-label">Username</label>
              <input type="text" value={username} onChange={(e) => setUsername(e.target.value)}
                required placeholder="Enter your username" className="sf-input" />
            </div>
            <div style={{ marginBottom: '24px' }}>
              <label className="sf-label">Password</label>
              <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                required placeholder="Enter your password" className="sf-input" />
            </div>
            {error && <div className="sf-alert sf-alert-error">{error}</div>}
            <button type="submit" disabled={loading} className="sf-btn sf-btn-primary"
              style={{ width: '100%', padding: '12px', fontSize: '15px' }}>
              {loading ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          <div style={{ marginTop: '20px', textAlign: 'center', borderTop: '1px solid var(--border)', paddingTop: '20px' }}>
            <span style={{ color: 'var(--text-muted)', fontSize: '14px' }}>Don't have an account? </span>
            <button onClick={onSwitchToRegister}
              style={{ background: 'none', border: 'none', color: 'var(--accent-light)', cursor: 'pointer', fontSize: '14px', fontWeight: '600', padding: 0 }}>
              Register
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
