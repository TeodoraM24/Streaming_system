import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

export const Register: React.FC<{ onSwitchToLogin: () => void }> = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    firstname: '',
    lastname: '',
    phonenumber: '',
    mail: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await register(formData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const field = (label: string, name: string, type = 'text', placeholder = '') => (
    <div style={{ marginBottom: '16px' }}>
      <label className="sf-label">{label}</label>
      <input type={type} name={name} value={(formData as any)[name]}
        onChange={handleChange} required placeholder={placeholder} className="sf-input" />
    </div>
  );

  return (
    <div style={{ minHeight: '100vh', background: 'var(--bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
      <div style={{ width: '100%', maxWidth: '440px' }}>
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{ fontSize: '30px', marginBottom: '8px' }}>▶</div>
          <div style={{ fontSize: '24px', fontWeight: '800', background: 'linear-gradient(135deg, #6366f1, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Streamflix
          </div>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px', fontSize: '14px' }}>Create your account</p>
        </div>

        <div style={{ background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '16px', padding: '32px' }}>
          <form onSubmit={handleSubmit}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
              {field('First Name', 'firstname', 'text', 'John')}
              {field('Last Name', 'lastname', 'text', 'Doe')}
            </div>
            {field('Username', 'username', 'text', 'johndoe')}
            {field('Email', 'mail', 'email', 'john@example.com')}
            {field('Phone Number', 'phonenumber', 'tel', '+45 12 34 56 78')}
            {field('Password', 'password', 'password', 'Min. 8 characters')}
            {error && <div className="sf-alert sf-alert-error">{error}</div>}
            <button type="submit" disabled={loading} className="sf-btn sf-btn-primary"
              style={{ width: '100%', padding: '12px', fontSize: '15px', marginTop: '8px' }}>
              {loading ? 'Creating account…' : 'Create Account'}
            </button>
          </form>

          <div style={{ marginTop: '20px', textAlign: 'center', borderTop: '1px solid var(--border)', paddingTop: '20px' }}>
            <span style={{ color: 'var(--text-muted)', fontSize: '14px' }}>Already have an account? </span>
            <button onClick={onSwitchToLogin}
              style={{ background: 'none', border: 'none', color: 'var(--accent-light)', cursor: 'pointer', fontSize: '14px', fontWeight: '600', padding: 0 }}>
              Sign In
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
