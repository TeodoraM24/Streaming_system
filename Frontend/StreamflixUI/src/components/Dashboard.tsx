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

  const tabs: { key: Tab; label: string }[] = [
    { key: 'movies', label: 'Movies' },
    { key: 'shows', label: 'Shows' },
    { key: 'genres', label: 'Genres' },
    { key: 'reviews', label: 'Reviews' },
    { key: 'profiles', label: 'Profiles' },
    { key: 'account', label: 'Account' },
    { key: 'plans', label: 'Plans' },
    { key: 'subscriptions', label: 'Subscriptions' },
    { key: 'receipts', label: 'Receipts' },
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
        return <Subscriptions />;
      case 'receipts':
        return <Receipts />;
      default:
        return <div>Select a tab</div>;
    }
  };

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
      <header style={{ background: '#333', color: 'white', padding: '15px 20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0, fontSize: '24px' }}>Streamflix</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
          <span>Welcome, {username}</span>
          <button onClick={logout} style={{ padding: '8px 16px', cursor: 'pointer', background: '#555', color: 'white', border: 'none', borderRadius: '4px' }}>
            Logout
          </button>
        </div>
      </header>

      <div style={{ display: 'flex', flex: 1 }}>
        <nav style={{ width: '200px', background: '#f5f5f5', padding: '20px', borderRight: '1px solid #ddd' }}>
          {tabs.map((tab) => (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              style={{
                display: 'block',
                width: '100%',
                padding: '10px',
                marginBottom: '10px',
                textAlign: 'left',
                background: activeTab === tab.key ? '#007bff' : 'white',
                color: activeTab === tab.key ? 'white' : 'black',
                border: '1px solid #ddd',
                borderRadius: '4px',
                cursor: 'pointer',
              }}
            >
              {tab.label}
            </button>
          ))}
        </nav>

        <main style={{ flex: 1, padding: '20px', overflowY: 'auto' }}>
          {renderContent()}
        </main>
      </div>
    </div>
  );
};
