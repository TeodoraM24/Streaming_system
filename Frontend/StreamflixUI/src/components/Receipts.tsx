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
      <h2>My Receipts</h2>
      {loading && <div>Loading...</div>}
      {error && <div style={{ color: 'red' }}>{error}</div>}

      {!loading && receipts.length === 0 && !error && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No receipts yet. Complete a subscription payment to generate a receipt.
        </div>
      )}

      {receipts.map((r) => (
        <div key={r.receiptId} style={{
          background: 'white', borderRadius: '8px', padding: '20px',
          marginBottom: '16px', border: '1px solid #ddd', borderLeft: '4px solid #28a745',
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <div style={{ fontSize: '11px', textTransform: 'uppercase', color: '#888', letterSpacing: '1px', marginBottom: '4px' }}>
                Receipt
              </div>
              <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#333' }}>
                #{r.receiptNumber}
              </div>
            </div>
            <span style={{ background: '#d4edda', color: '#155724', padding: '4px 10px', borderRadius: '12px', fontSize: '13px', fontWeight: 'bold' }}>
              ✓ PAID
            </span>
          </div>
          <hr style={{ border: 'none', borderTop: '1px solid #eee', margin: '12px 0' }} />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', fontSize: '14px' }}>
            <div><span style={{ color: '#888' }}>Amount</span><br /><strong>{r.price != null ? r.price : '—'}</strong></div>
            <div><span style={{ color: '#888' }}>Payment ID</span><br /><strong>#{r.paymentId}</strong></div>
            <div style={{ gridColumn: '1 / -1' }}>
              <span style={{ color: '#888' }}>Date</span><br />
              <strong>{r.paydate ? new Date(r.paydate).toLocaleString() : '—'}</strong>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};
