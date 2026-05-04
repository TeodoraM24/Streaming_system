import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

const inp: React.CSSProperties = { width: '100%', padding: '9px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #ccc', fontSize: '14px' };
const lbl: React.CSSProperties = { display: 'block', marginBottom: '5px', fontWeight: 'bold', fontSize: '13px' };

const emptyCard = { type: 'CARD', cardNumber: '', expirationMonth: '', expirationYear: '', cvc: '' };

type View = 'sub' | 'choosePlan' | 'payForm' | 'receipt';

type CardState = typeof emptyCard;

const F = ({ label, children }: { label: string; children: React.ReactNode }) => (
  <div style={{ marginBottom: '14px' }}>
    <label style={lbl}>{label}</label>
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
      <select value={card.type} onChange={e => setCard(c => ({ ...c, type: e.target.value }))} style={inp}>
        <option value="CARD">Credit / Debit Card</option>
        <option value="MOBILEPAY">MobilePay</option>
      </select>
    </F>
    <F label="Card Number">
      <input type="text" maxLength={16} placeholder="1234567890123456" value={card.cardNumber}
        onChange={e => setCard(c => ({ ...c, cardNumber: e.target.value.replace(/\D/g, '') }))}
        required pattern="\d{16}" style={inp} />
    </F>
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '12px', marginBottom: '14px' }}>
      <div><label style={lbl}>Month</label>
        <input type="number" min={1} max={12} placeholder="MM" value={card.expirationMonth}
          onChange={e => setCard(c => ({ ...c, expirationMonth: e.target.value }))} required style={inp} /></div>
      <div><label style={lbl}>Year</label>
        <input type="number" min={2026} placeholder="YYYY" value={card.expirationYear}
          onChange={e => setCard(c => ({ ...c, expirationYear: e.target.value }))} required style={inp} /></div>
      <div><label style={lbl}>CVC</label>
        <input type="text" maxLength={3} placeholder="123" value={card.cvc}
          onChange={e => setCard(c => ({ ...c, cvc: e.target.value.replace(/\D/g, '') }))}
          required pattern="\d{3}" style={inp} /></div>
    </div>
    <div style={{ display: 'flex', gap: '8px' }}>
      <button type="submit" disabled={loading}
        style={{ flex: 1, padding: '11px', cursor: 'pointer', background: '#28a745', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold', fontSize: '15px' }}>
        {loading ? 'Processing...' : submitLabel}
      </button>
      <button type="button" onClick={onBack}
        style={{ padding: '11px 18px', cursor: 'pointer', background: '#f0f0f0', border: 'none', borderRadius: '4px' }}>
        ← Back
      </button>
    </div>
  </form>
);

export const Subscriptions: React.FC = () => {
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

  const handleExistingSubscriptionPayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!subscription) return;
    setLoading(true);
    setError('');
    try {
      const receiptData = await processPayment(subscription.subscriptionId, currentPlan);
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
    <div style={{ maxWidth: '600px' }}>
      <h2>My Subscription</h2>
      {error && <div style={{ color: 'red', marginBottom: '10px', padding: '8px', background: '#fff3f3', borderRadius: '4px' }}>{error}</div>}
      {loading && <div>Loading...</div>}

      {/* ── ACTIVE SUBSCRIPTION ── */}
      {view === 'sub' && subscription && !loading && (
        <div style={{ background: 'white', padding: '24px', borderRadius: '8px', border: '2px solid #007bff' }}>
          <h3 style={{ marginTop: 0, color: '#007bff' }}>Active Subscription</h3>

          <div style={{ background: '#e8f4fd', border: '1px solid #b8daff', borderRadius: '6px', padding: '12px 16px', marginBottom: '14px' }}>
            <div style={{ fontSize: '11px', color: '#555', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '2px' }}>Current Plan</div>
            <div style={{ fontSize: '22px', fontWeight: 'bold', color: '#007bff' }}>
              {currentPlan ? currentPlan.name : `Plan #${subscription.planId}`}
            </div>
            {currentPlan && (
              <div style={{ fontSize: '14px', color: '#333', marginTop: '2px' }}>
                {currentPlan.price} {currentPlan.currency} / month
                {currentPlan.description && <span style={{ color: '#666', marginLeft: '8px' }}>— {currentPlan.description}</span>}
              </div>
            )}
          </div>

          <p style={{ margin: '5px 0' }}><strong>Status:</strong> <span style={{ color: subscription.status === 'ACTIVE' ? 'green' : 'orange' }}>{subscription.status}</span></p>
          <p style={{ margin: '5px 0' }}><strong>Start Date:</strong> {subscription.startdate}</p>
          <p style={{ margin: '5px 0' }}><strong>End Date:</strong> {subscription.enddate}</p>
          <p style={{ margin: '5px 0' }}><strong>Next Bill Date:</strong> {subscription.nextBillDate}</p>

          {!showChangePlan && (
            <div style={{ display: 'flex', gap: '8px', marginTop: '16px', flexWrap: 'wrap' }}>
              <button onClick={() => setView('payForm')}
                style={{ padding: '8px 16px', cursor: 'pointer', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px' }}>
                💳 Pay & Get Receipt
              </button>
              <button onClick={() => { setShowChangePlan(true); setChangePlanId(''); }}
                style={{ padding: '8px 16px', cursor: 'pointer', background: '#28a745', color: 'white', border: 'none', borderRadius: '4px' }}>
                Change Plan
              </button>
              <button onClick={handleCancel}
                style={{ padding: '8px 16px', cursor: 'pointer', background: '#dc3545', color: 'white', border: 'none', borderRadius: '4px' }}>
                Cancel Subscription
              </button>
            </div>
          )}

          {showChangePlan && (
            <form onSubmit={handleChangePlan} style={{ marginTop: '14px', background: '#f8f9fa', padding: '16px', borderRadius: '6px', border: '1px solid #dee2e6' }}>
              <F label="Select New Plan">
                <select value={changePlanId} onChange={e => setChangePlanId(e.target.value)} required style={inp}>
                  <option value="">-- Choose a plan --</option>
                  {activePlans.filter(p => p.planId !== subscription.planId).map((p: any) => (
                    <option key={p.planId} value={p.planId}>{p.name} — {p.price} {p.currency}</option>
                  ))}
                </select>
              </F>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button type="submit" disabled={loading}
                  style={{ padding: '8px 16px', cursor: 'pointer', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px' }}>
                  {loading ? 'Saving...' : 'Confirm Change'}
                </button>
                <button type="button" onClick={() => setShowChangePlan(false)}
                  style={{ padding: '8px 16px', cursor: 'pointer', background: '#f0f0f0', border: 'none', borderRadius: '4px' }}>
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>
      )}

      {/* ── CHOOSE PLAN (no subscription) ── */}
      {view === 'choosePlan' && !loading && (
        <div style={{ background: 'white', padding: '24px', borderRadius: '8px', border: '1px solid #ddd' }}>
          <h3 style={{ marginTop: 0 }}>Step 1 of 2 — Choose a Plan</h3>
          <F label="Select a Plan">
            <select value={selectedPlanId} onChange={e => setSelectedPlanId(e.target.value)} style={inp}>
              <option value="">-- Choose a plan --</option>
              {activePlans.map((p: any) => (
                <option key={p.planId} value={p.planId}>{p.name} — {p.price} {p.currency}</option>
              ))}
            </select>
          </F>
          {checkoutPlan && (
            <div style={{ background: '#f0fff4', border: '1px solid #c3e6cb', borderRadius: '6px', padding: '10px 14px', marginBottom: '14px' }}>
              <strong>{checkoutPlan.name}</strong> — {checkoutPlan.price} {checkoutPlan.currency}/month
              {checkoutPlan.description && <div style={{ fontSize: '13px', color: '#555', marginTop: '3px' }}>{checkoutPlan.description}</div>}
            </div>
          )}
          <button onClick={() => { if (selectedPlanId) setView('payForm'); }} disabled={!selectedPlanId}
            style={{ padding: '10px 24px', cursor: 'pointer', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
            Next: Payment →
          </button>
        </div>
      )}

      {/* ── PAYMENT FORM ── */}
      {view === 'payForm' && !loading && (
        <div style={{ background: 'white', padding: '24px', borderRadius: '8px', border: '1px solid #ddd' }}>
          {subscription ? (
            <>
              <h3 style={{ marginTop: 0 }}>Pay for Your Subscription</h3>
              <div style={{ background: '#e8f4fd', border: '1px solid #b8daff', borderRadius: '6px', padding: '10px 14px', marginBottom: '18px', fontSize: '14px' }}>
                Paying <strong>{currentPlan?.price} {currentPlan?.currency}/month</strong> for <strong>{currentPlan?.name}</strong>
              </div>
              <CardForm card={card} setCard={setCard} onSubmit={handleExistingSubscriptionPayment} loading={loading}
                submitLabel={`💳 Pay ${currentPlan?.price ?? ''} ${currentPlan?.currency ?? ''}`}
                onBack={() => setView('sub')} />
            </>
          ) : (
            <>
              <h3 style={{ marginTop: 0 }}>Step 2 of 2 — Payment Details</h3>
              <div style={{ background: '#e8f4fd', border: '1px solid #b8daff', borderRadius: '6px', padding: '10px 14px', marginBottom: '18px', fontSize: '14px' }}>
                Subscribing to <strong>{checkoutPlan?.name}</strong> — <strong>{checkoutPlan?.price} {checkoutPlan?.currency}/month</strong>
              </div>
              <CardForm card={card} setCard={setCard} onSubmit={handleNewSubscriptionPayment} loading={loading}
                submitLabel={`💳 Pay ${checkoutPlan?.price ?? ''} ${checkoutPlan?.currency ?? ''}`}
                onBack={() => setView('choosePlan')} />
            </>
          )}
        </div>
      )}

      {/* ── RECEIPT ── */}
      {view === 'receipt' && receipt && (
        <div style={{ background: 'white', padding: '24px', borderRadius: '8px', border: '2px solid #28a745' }}>
          <div style={{ textAlign: 'center', marginBottom: '16px' }}>
            <div style={{ fontSize: '48px' }}>✅</div>
            <h3 style={{ color: '#28a745', margin: '4px 0 0' }}>Payment Successful!</h3>
          </div>
          <div style={{ background: '#f8f9fa', borderRadius: '6px', padding: '16px', borderLeft: '4px solid #28a745' }}>
            <div style={{ fontSize: '11px', textTransform: 'uppercase', color: '#888', letterSpacing: '1px', marginBottom: '10px' }}>
              {receipt.backend ? `Receipt ${receipt.backend.receiptNumber}` : 'Payment Confirmation'}
            </div>
            <p style={{ margin: '6px 0' }}><strong>Plan:</strong> {receipt.plan?.name}</p>
            <p style={{ margin: '6px 0' }}><strong>Amount:</strong> {receipt.backend?.price ?? receipt.plan?.price} {receipt.plan?.currency}</p>
            <p style={{ margin: '6px 0' }}><strong>Card:</strong> {receipt.type} •••• {receipt.last4}</p>
            <p style={{ margin: '6px 0' }}><strong>Date:</strong> {receipt.backend?.paydate ? new Date(receipt.backend.paydate).toLocaleString() : new Date().toLocaleString()}</p>
            <p style={{ margin: '6px 0' }}><strong>Status:</strong> <span style={{ color: 'green', fontWeight: 'bold' }}>✓ PAID</span></p>
            {receipt.backend?.receiptId && <p style={{ margin: '6px 0' }}><strong>Receipt ID:</strong> #{receipt.backend.receiptId}</p>}
          </div>
          <button onClick={() => setView('sub')}
            style={{ width: '100%', padding: '10px', marginTop: '16px', cursor: 'pointer', background: '#007bff', color: 'white', border: 'none', borderRadius: '4px', fontWeight: 'bold' }}>
            View My Subscription
          </button>
        </div>
      )}
    </div>
  );
};
