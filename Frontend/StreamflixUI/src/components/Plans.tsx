import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

export const Plans: React.FC = () => {
  const [plans, setPlans] = useState<any[]>([]);
  const [myPlanId, setMyPlanId] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loadPlans = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.getPlans();
      setPlans(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load plans');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPlans();
    api.getMySubscription()
      .then((sub: any) => setMyPlanId(sub.planId ?? null))
      .catch(() => setMyPlanId(null));
  }, []);

  return (
    <div>
      <h2>Subscription Plans</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px' }}>{error}</div>}
      {loading && <div>Loading...</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
        {plans.map((plan) => (
          <div
            key={plan.planId}
            style={{
              border: plan.active ? '2px solid #28a745' : '2px solid #ddd',
              borderRadius: '8px',
              padding: '20px',
              background: plan.active ? 'white' : '#f5f5f5',
              opacity: plan.active ? 1 : 0.7,
            }}
          >
            <h3 style={{ marginTop: 0, color: '#007bff' }}>{plan.name}</h3>
            <div style={{ fontSize: '32px', fontWeight: 'bold', margin: '15px 0' }}>
              {plan.price} {plan.currency}
            </div>
            <p style={{ color: '#666', marginBottom: '15px' }}>{plan.description}</p>
            <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginTop: '15px' }}>
              {plan.active ? (
                <span style={{ color: 'green', fontWeight: 'bold' }}>✓ Available</span>
              ) : (
                <span style={{ color: '#aaa', fontWeight: 'bold' }}>✗ Unavailable</span>
              )}
              {myPlanId === plan.planId && (
                <span style={{ background: '#007bff', color: 'white', fontSize: '11px', fontWeight: 'bold', padding: '2px 8px', borderRadius: '12px' }}>
                  YOUR PLAN
                </span>
              )}
            </div>
          </div>
        ))}
      </div>

      {!loading && plans.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No plans available
        </div>
      )}
    </div>
  );
};
