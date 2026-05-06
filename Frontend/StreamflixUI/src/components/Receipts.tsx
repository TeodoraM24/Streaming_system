import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const Receipts: React.FC = () => {
  const [receipts, setReceipts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    api.getMyReceipts()
      .then(data => setReceipts(data.slice().reverse()))
      .catch(err => setError(err instanceof Error ? err.message : 'Failed to load receipts'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div style={{ maxWidth: '700px' }}>
      <div className="sf-page-header" style={{ marginBottom: '28px' }}>
        <h2 className="sf-page-title">🧾 My Receipts</h2>
        <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>{receipts.length} receipt{receipts.length !== 1 ? 's' : ''}</span>
      </div>

      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}
      {error && <div className="sf-alert sf-alert-error">{error}</div>}

      {!loading && receipts.length === 0 && !error && (
        <div className="sf-empty">🧾 No receipts yet. Complete a subscription payment to generate one.</div>
      )}

      {receipts.map((r) => (
        <div key={r.receiptId} style={{
          background: 'var(--surface-2)',
          borderRadius: '14px',
          padding: '22px 24px',
          marginBottom: '14px',
          border: '1px solid var(--border)',
          borderLeft: '4px solid var(--success)',
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <div style={{ fontSize: '10px', textTransform: 'uppercase', letterSpacing: '1.2px', color: 'var(--text-dim)', marginBottom: '4px', fontWeight: '700' }}>
                Receipt
              </div>
              <div style={{ fontSize: '20px', fontWeight: '700', color: 'var(--text)' }}>
                #{r.receiptNumber}
              </div>
            </div>
            <span className="sf-badge sf-badge-success">✓ Paid</span>
          </div>
          <hr style={{ border: 'none', borderTop: '1px solid var(--border)', margin: '16px 0' }} />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', fontSize: '14px' }}>
            <div>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>Amount</div>
              <div style={{ fontSize: '18px', fontWeight: '700', color: 'var(--success)' }}>{r.price != null ? r.price : '—'}</div>
            </div>
            <div>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>Payment ID</div>
              <div style={{ fontWeight: '600', color: 'var(--text-muted)' }}>#{r.paymentId}</div>
            </div>
            <div style={{ gridColumn: '1 / -1' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>Date</div>
              <div style={{ fontWeight: '600', color: 'var(--text-muted)' }}>{r.paydate ? new Date(r.paydate).toLocaleString() : '—'}</div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};
