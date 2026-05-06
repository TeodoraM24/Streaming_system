import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const emptyCard = { type: 'CARD', cardNumber: '', expirationMonth: '', expirationYear: '', cvc: '' };

type View = 'sub' | 'choosePlan' | 'payForm' | 'receipt';

type CardState = typeof emptyCard;

const F = ({ label, children }: { label: string; children: React.ReactNode }) => (
  <div style={{ marginBottom: '16px' }}>
    <label className="sf-label">{label}</label>
    {children}
  </div>
);

const CardForm = ({
  card, setCard, onSubmit, submitLabel, loading, onBack,
}: {
  card: CardState;
  setCard: React.Dispatch<React.SetStateAction<CardState>>;
  onSubmit: (e: React.FormEvent) => void;
  submitLabel: string;
  loading: boolean;
  onBack: () => void;
}) => (
  <form onSubmit={onSubmit}>
    <F label="Payment Type">
      <select value={card.type} onChange={e => setCard(c => ({ ...c, type: e.target.value }))} className="sf-input">
        <option value="CARD">Credit / Debit Card</option>
        <option value="MOBILEPAY">MobilePay</option>
      </select>
    </F>
    <F label="Card Number">
      <input type="text" maxLength={16} placeholder="1234 5678 9012 3456" value={card.cardNumber}
        onChange={e => setCard(c => ({ ...c, cardNumber: e.target.value.replace(/\D/g, '') }))}
        required pattern="\d{16}" className="sf-input" />
    </F>
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px', marginBottom: '16px' }}>
      <div><label className="sf-label">Month</label>
        <input type="number" min={1} max={12} placeholder="MM" value={card.expirationMonth}
          onChange={e => setCard(c => ({ ...c, expirationMonth: e.target.value }))} required className="sf-input" /></div>
      <div><label className="sf-label">Year</label>
        <input type="number" min={2026} placeholder="YYYY" value={card.expirationYear}
          onChange={e => setCard(c => ({ ...c, expirationYear: e.target.value }))} required className="sf-input" /></div>
      <div><label className="sf-label">CVC</label>
        <input type="text" maxLength={3} placeholder="123" value={card.cvc}
          onChange={e => setCard(c => ({ ...c, cvc: e.target.value.replace(/\D/g, '') }))}
          required pattern="\d{3}" className="sf-input" /></div>
    </div>
    <div style={{ display: 'flex', gap: '8px' }}>
      <button type="submit" disabled={loading} className="sf-btn sf-btn-success" style={{ flex: 1, padding: '12px', fontSize: '14px' }}>
        {loading ? 'Processing…' : submitLabel}
      </button>
      <button type="button" onClick={onBack} className="sf-btn sf-btn-ghost">
        ← Back
      </button>
    </div>
  </form>
);

export const Subscriptions: React.FC<{ onNavigate?: (tab: string) => void }> = ({ onNavigate }) => {
  const { accountId } = useAuth();

  const [subscription, setSubscription] = useState<any>(null);
  const [allPlans, setAllPlans] = useState<any[]>([]);
  const [activePlans, setActivePlans] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [view, setView] = useState<View>('sub');

  const [selectedPlanId, setSelectedPlanId] = useState('');
  const [card, setCard] = useState(emptyCard);
  const [receipt, setReceipt] = useState<any>(null);

  const [showChangePlan, setShowChangePlan] = useState(false);
  const [changePlanId, setChangePlanId] = useState('');

  const planMap: Record<number, any> = Object.fromEntries(allPlans.map(p => [p.planId, p]));
  const currentPlan = subscription ? planMap[subscription.planId] : null;
  const checkoutPlan = planMap[parseInt(selectedPlanId)] ?? null;

  useEffect(() => {
    setLoading(true);
    Promise.all([api.getMySubscription().catch(() => null), api.getPlans().catch(() => [])])
      .then(([sub, plans]) => {
        setSubscription(sub);
        setAllPlans(plans);
        setActivePlans(plans.filter((p: any) => p.active));
        setView(sub ? 'sub' : 'choosePlan');
      })
      .finally(() => setLoading(false));
  }, []);

  const processPayment = async (subscriptionId: number, plan: any) => {
    const savedMethod = await api.createPaymentMethod({
      cardNumber: card.cardNumber,
      expirationMonth: parseInt(card.expirationMonth),
      expirationYear: parseInt(card.expirationYear),
      cvc: card.cvc,
      type: card.type,
      defaultPaymentmethod: true,
      accountId,
    });
    await api.createPayment({
      subscriptionId,
      paymentMethodId: savedMethod.paymentmethodId,
      price: plan?.price,
      currency: plan?.currency,
      status: 'PAID',
    });
    const receipts = await api.getMyReceipts();
    const latestReceipt = receipts.length > 0 ? receipts[receipts.length - 1] : null;
    return { last4: card.cardNumber.slice(-4), type: card.type, plan, backend: latestReceipt };
  };

  const handleNewSubscriptionPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!accountId) { setError('Account ID not available'); return; }
    setLoading(true);
    setError('');
    try {
      const sub = await api.createSubscription({ accountId, planId: parseInt(selectedPlanId) });
      const receiptData = await processPayment(sub.subscriptionId, checkoutPlan);
      setSubscription(sub);
      setReceipt(receiptData);
      setCard(emptyCard);
      setView('receipt');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Payment failed');
    } finally {
      setLoading(false);
    }
  };


  const handleChangePlan = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subscription) return;
    setLoading(true);
    setError('');
    try {
      await api.patchSubscription(subscription.subscriptionId, { planId: parseInt(changePlanId) });
      const updated = await api.getMySubscription();
      setSubscription(updated);
      setShowChangePlan(false);
      setChangePlanId('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to change plan');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async () => {
    if (!subscription || !confirm('Cancel your subscription?')) return;
    setLoading(true);
    setError('');
    try {
      await api.deleteSubscription(subscription.subscriptionId);
      setSubscription(null);
      setView('choosePlan');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to cancel');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '640px' }}>
      <div className="sf-page-header" style={{ marginBottom: '24px' }}>
        <h2 className="sf-page-title">📋 My Subscription</h2>
      </div>
      {error && <div className="sf-alert sf-alert-error">{error}</div>}
      {loading && <div style={{ color: 'var(--text-muted)', padding: '20px 0' }}>Loading…</div>}

      {/* ── ACTIVE SUBSCRIPTION ── */}
      {view === 'sub' && subscription && !loading && (
        <div style={{ background: 'var(--surface-2)', padding: '28px', borderRadius: '16px', border: '2px solid var(--accent)' }}>
          <h3 style={{ color: 'var(--accent-light)', marginBottom: '20px', fontSize: '16px' }}>✓ Active Subscription</h3>

          <div style={{ background: 'rgba(99,102,241,0.1)', border: '1px solid rgba(99,102,241,0.3)', borderRadius: '10px', padding: '16px 20px', marginBottom: '18px' }}>
            <div style={{ fontSize: '11px', color: 'var(--text-dim)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '4px', fontWeight: '700' }}>Current Plan</div>
            <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--accent-light)' }}>
              {currentPlan ? currentPlan.name : `Plan #${subscription.planId}`}
            </div>
            {currentPlan && (
              <div style={{ fontSize: '14px', color: 'var(--text-muted)', marginTop: '4px' }}>
                {currentPlan.price} {currentPlan.currency} / month
                {currentPlan.description && <span style={{ marginLeft: '8px' }}>— {currentPlan.description}</span>}
              </div>
            )}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '20px' }}>
            <div style={{ background: 'var(--surface-3)', borderRadius: '8px', padding: '12px' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>Status</div>
              <span className={`sf-badge ${subscription.status === 'ACTIVE' ? 'sf-badge-success' : 'sf-badge-warning'}`}>{subscription.status}</span>
            </div>
            <div style={{ background: 'var(--surface-3)', borderRadius: '8px', padding: '12px' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>Start Date</div>
              <div style={{ fontSize: '14px', fontWeight: '600' }}>{subscription.startdate ?? '—'}</div>
            </div>
            <div style={{ background: 'var(--surface-3)', borderRadius: '8px', padding: '12px' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>End Date</div>
              <div style={{ fontSize: '14px', fontWeight: '600' }}>{subscription.enddate ?? '—'}</div>
            </div>
            <div style={{ background: 'var(--surface-3)', borderRadius: '8px', padding: '12px' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-dim)', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '0.8px', marginBottom: '4px' }}>Next Bill</div>
              <div style={{ fontSize: '14px', fontWeight: '600' }}>{subscription.nextBillDate ?? '—'}</div>
            </div>
          </div>

          {!showChangePlan && (
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              <button onClick={() => onNavigate?.('receipts')} className="sf-btn sf-btn-primary">🧾 Get Receipt</button>
              <button onClick={() => { setShowChangePlan(true); setChangePlanId(''); }} className="sf-btn sf-btn-success">✏️ Change Plan</button>
              <button onClick={handleCancel} className="sf-btn sf-btn-danger">✕ Cancel</button>
            </div>
          )}

          {showChangePlan && (
            <form onSubmit={handleChangePlan} style={{ marginTop: '16px', background: 'var(--surface-3)', padding: '20px', borderRadius: '10px', border: '1px solid var(--border)' }}>
              <F label="Select New Plan">
                <select value={changePlanId} onChange={e => setChangePlanId(e.target.value)} required className="sf-input">
                  <option value="">— Choose a plan —</option>
                  {activePlans.filter(p => p.planId !== subscription.planId).map((p: any) => (
                    <option key={p.planId} value={p.planId}>{p.name} — {p.price} {p.currency}</option>
                  ))}
                </select>
              </F>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button type="submit" disabled={loading} className="sf-btn sf-btn-primary">
                  {loading ? 'Saving…' : 'Confirm Change'}
                </button>
                <button type="button" onClick={() => setShowChangePlan(false)} className="sf-btn sf-btn-ghost">Cancel</button>
              </div>
            </form>
          )}
        </div>
      )}

      {/* ── CHOOSE PLAN (no subscription) ── */}
      {view === 'choosePlan' && !loading && (
        <div style={{ background: 'var(--surface-2)', padding: '28px', borderRadius: '16px', border: '1px solid var(--border)' }}>
          <h3 style={{ marginBottom: '20px', color: 'var(--text)', fontSize: '16px' }}>Step 1 of 2 — Choose a Plan</h3>
          <F label="Select a Plan">
            <select value={selectedPlanId} onChange={e => setSelectedPlanId(e.target.value)} className="sf-input">
              <option value="">— Choose a plan —</option>
              {activePlans.map((p: any) => (
                <option key={p.planId} value={p.planId}>{p.name} — {p.price} {p.currency}</option>
              ))}
            </select>
          </F>
          {checkoutPlan && (
            <div style={{ background: 'var(--success-bg)', border: '1px solid rgba(34,197,94,0.3)', borderRadius: '8px', padding: '12px 16px', marginBottom: '16px' }}>
              <strong style={{ color: 'var(--success)' }}>{checkoutPlan.name}</strong>
              <span style={{ color: 'var(--text-muted)', marginLeft: '8px' }}>{checkoutPlan.price} {checkoutPlan.currency}/month</span>
              {checkoutPlan.description && <div style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '4px' }}>{checkoutPlan.description}</div>}
            </div>
          )}
          <button onClick={() => { if (selectedPlanId) setView('payForm'); }} disabled={!selectedPlanId}
            className="sf-btn sf-btn-primary" style={{ padding: '12px 28px', fontSize: '14px' }}>
            Next: Payment →
          </button>
        </div>
      )}

      {/* ── PAYMENT FORM (new subscriptions only) ── */}
      {view === 'payForm' && !loading && (
        <div style={{ background: 'var(--surface-2)', padding: '28px', borderRadius: '16px', border: '1px solid var(--border)' }}>
          <h3 style={{ marginBottom: '20px', color: 'var(--text)', fontSize: '16px' }}>Step 2 of 2 — Payment Details</h3>
          <div style={{ background: 'rgba(99,102,241,0.1)', border: '1px solid rgba(99,102,241,0.3)', borderRadius: '8px', padding: '12px 16px', marginBottom: '20px', fontSize: '14px' }}>
            Subscribing to <strong style={{ color: 'var(--accent-light)' }}>{checkoutPlan?.name}</strong>
            <span style={{ color: 'var(--text-muted)', marginLeft: '8px' }}>— {checkoutPlan?.price} {checkoutPlan?.currency}/month</span>
          </div>
          <CardForm card={card} setCard={setCard} onSubmit={handleNewSubscriptionPayment} loading={loading}
            submitLabel={`💳 Pay ${checkoutPlan?.price ?? ''} ${checkoutPlan?.currency ?? ''}`}
            onBack={() => setView('choosePlan')} />
        </div>
      )}

      {/* ── RECEIPT ── */}
      {view === 'receipt' && receipt && (
        <div style={{ background: 'var(--surface-2)', padding: '28px', borderRadius: '16px', border: '2px solid var(--success)' }}>
          <div style={{ textAlign: 'center', marginBottom: '24px' }}>
            <div style={{ fontSize: '52px', marginBottom: '8px' }}>✅</div>
            <h3 style={{ color: 'var(--success)', fontSize: '20px' }}>Payment Successful!</h3>
          </div>
          <div style={{ background: 'var(--surface-3)', borderRadius: '10px', padding: '20px', borderLeft: '4px solid var(--success)' }}>
            <div style={{ fontSize: '10px', textTransform: 'uppercase', color: 'var(--text-dim)', letterSpacing: '1.2px', fontWeight: '700', marginBottom: '14px' }}>
              {receipt.backend ? `Receipt ${receipt.backend.receiptNumber}` : 'Payment Confirmation'}
            </div>
            {[['Plan', receipt.plan?.name], ['Amount', `${receipt.backend?.price ?? receipt.plan?.price} ${receipt.plan?.currency}`],
              ['Card', `${receipt.type} •••• ${receipt.last4}`],
              ['Date', receipt.backend?.paydate ? new Date(receipt.backend.paydate).toLocaleString() : new Date().toLocaleString()],
              ['Status', '✓ PAID'],
              ...(receipt.backend?.receiptId ? [['Receipt ID', `#${receipt.backend.receiptId}`]] : [])].map(([k, v]) => (
              <div key={k} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid var(--border)', fontSize: '14px' }}>
                <span style={{ color: 'var(--text-muted)' }}>{k}</span>
                <strong style={{ color: k === 'Status' ? 'var(--success)' : 'var(--text)' }}>{v as string}</strong>
              </div>
            ))}
          </div>
          <button onClick={() => setView('sub')} className="sf-btn sf-btn-primary"
            style={{ width: '100%', marginTop: '20px', padding: '12px', fontSize: '14px' }}>
            View My Subscription
          </button>
        </div>
      )}
    </div>
  );
};
