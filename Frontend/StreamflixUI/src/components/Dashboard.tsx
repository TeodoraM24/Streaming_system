import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Movies } from './Movies';
import { Shows } from './Shows';
import { Genres } from './Genres';
import { Account } from './Account';
import { Profiles } from './Profiles';
import { Reviews } from './Reviews';
import { Plans } from './Plans';
import { Subscriptions } from './Subscriptions';
import { Receipts } from './Receipts';

type Tab = 'movies' | 'shows' | 'genres' | 'account' | 'profiles' | 'reviews' | 'plans' | 'subscriptions' | 'receipts';

export const Dashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState<Tab>('movies');
  const { username, logout } = useAuth();

  const tabs: { key: Tab; label: string; icon: string }[] = [
    { key: 'movies', label: 'Movies', icon: '🎬' },
    { key: 'shows', label: 'TV Shows', icon: '📺' },
    { key: 'genres', label: 'Genres', icon: '🎭' },
    { key: 'reviews', label: 'Reviews', icon: '⭐' },
    { key: 'profiles', label: 'Profiles', icon: '👤' },
    { key: 'account', label: 'Account', icon: '⚙️' },
    { key: 'plans', label: 'Plans', icon: '💎' },
    { key: 'subscriptions', label: 'Subscriptions', icon: '📋' },
    { key: 'receipts', label: 'Receipts', icon: '🧾' },
  ];

  const renderContent = () => {
    switch (activeTab) {
      case 'movies':
        return <Movies />;
      case 'shows':
        return <Shows />;
      case 'genres':
        return <Genres />;
      case 'account':
        return <Account />;
      case 'profiles':
        return <Profiles />;
      case 'reviews':
        return <Reviews />;
      case 'plans':
        return <Plans />;
      case 'subscriptions':
        return <Subscriptions onNavigate={(tab) => setActiveTab(tab as Tab)} />;
      case 'receipts':
        return <Receipts />;
      default:
        return <div>Select a tab</div>;
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', background: 'var(--bg)' }}>
      <header style={{
        background: 'var(--surface)',
        borderBottom: '1px solid var(--border)',
        padding: '0 24px',
        height: '60px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        position: 'sticky',
        top: 0,
        zIndex: 100,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <span style={{ fontSize: '22px' }}>▶</span>
          <span style={{ fontSize: '18px', fontWeight: '800', background: 'linear-gradient(135deg, #6366f1, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            Streamflix
          </span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <span style={{ fontSize: '13px', color: 'var(--text-muted)' }}>
            👤 <strong style={{ color: 'var(--text)' }}>{username}</strong>
          </span>
          <button onClick={logout} className="sf-btn sf-btn-ghost" style={{ fontSize: '13px', padding: '7px 14px' }}>
            Logout
          </button>
        </div>
      </header>

      <div style={{ display: 'flex', flex: 1 }}>
        <nav style={{
          width: '220px',
          background: 'var(--surface)',
          borderRight: '1px solid var(--border)',
          padding: '20px 12px',
          flexShrink: 0,
        }}>
          <div style={{ fontSize: '10px', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '1.2px', color: 'var(--text-dim)', marginBottom: '10px', padding: '0 12px' }}>
            Browse
          </div>
          {tabs.slice(0, 3).map((tab) => (
            <button key={tab.key} onClick={() => setActiveTab(tab.key)}
              className={`sf-nav-item${activeTab === tab.key ? ' active' : ''}`}>
              <span>{tab.icon}</span> {tab.label}
            </button>
          ))}
          <div style={{ fontSize: '10px', fontWeight: '700', textTransform: 'uppercase', letterSpacing: '1.2px', color: 'var(--text-dim)', margin: '16px 0 10px', padding: '0 12px' }}>
            Account
          </div>
          {tabs.slice(3).map((tab) => (
            <button key={tab.key} onClick={() => setActiveTab(tab.key)}
              className={`sf-nav-item${activeTab === tab.key ? ' active' : ''}`}>
              <span>{tab.icon}</span> {tab.label}
            </button>
          ))}
        </nav>

        <main style={{ flex: 1, padding: '28px 32px', overflowY: 'auto', background: 'var(--bg)' }}>
          {renderContent()}
        </main>
      </div>
    </div>
  );
};
