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
      <div className="sf-page-header" style={{ marginBottom: '32px' }}>
        <h2 className="sf-page-title">💎 Subscription Plans</h2>
      </div>

      {error && <div className="sf-alert sf-alert-error">{error}</div>}
      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px' }}>
        {plans.map((plan) => {
          const isMyPlan = myPlanId === plan.planId;
          return (
            <div key={plan.planId} style={{
              background: 'var(--surface-2)',
              border: `2px solid ${isMyPlan ? 'var(--accent)' : plan.active ? 'var(--border)' : 'var(--border)'}`,
              borderRadius: '16px',
              padding: '28px 24px',
              opacity: plan.active ? 1 : 0.5,
              position: 'relative',
              boxShadow: isMyPlan ? '0 0 24px rgba(99,102,241,0.2)' : 'none',
            }}>
              {isMyPlan && (
                <div style={{ position: 'absolute', top: '-12px', left: '50%', transform: 'translateX(-50%)' }}>
                  <span className="sf-badge sf-badge-primary">✓ Your Plan</span>
                </div>
              )}
              <h3 style={{ fontSize: '18px', fontWeight: '700', color: isMyPlan ? 'var(--accent-light)' : 'var(--text)', marginBottom: '16px' }}>
                {plan.name}
              </h3>
              <div style={{ fontSize: '36px', fontWeight: '800', color: 'var(--text)', marginBottom: '4px', lineHeight: 1 }}>
                {plan.price}
                <span style={{ fontSize: '16px', fontWeight: '400', color: 'var(--text-muted)', marginLeft: '4px' }}>
                  {plan.currency}/mo
                </span>
              </div>
              <p style={{ color: 'var(--text-muted)', fontSize: '14px', margin: '16px 0', lineHeight: '1.5' }}>
                {plan.description}
              </p>
              <div style={{ marginTop: '20px', paddingTop: '16px', borderTop: '1px solid var(--border)' }}>
                {plan.active
                  ? <span className="sf-badge sf-badge-success">✓ Available</span>
                  : <span className="sf-badge sf-badge-danger">✗ Unavailable</span>}
              </div>
            </div>
          );
        })}
      </div>

      {!loading && plans.length === 0 && (
        <div className="sf-empty">💎 No plans available</div>
      )}
    </div>
  );
};
