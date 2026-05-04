import React from 'react';

interface VideoPlayerProps {
  title: string;
  type: 'movie' | 'show';
  duration?: number;
  onClose: () => void;
}

export const VideoPlayer: React.FC<VideoPlayerProps> = ({ title, type, duration, onClose }) => {
  return (
    <div
      style={{
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.9)',
        zIndex: 1000,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
      onClick={onClose}
    >
      <div
        style={{
          width: '90%',
          maxWidth: '1200px',
          backgroundColor: '#000',
          borderRadius: '8px',
          overflow: 'hidden',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div
          style={{
            position: 'relative',
            paddingTop: '56.25%',
            backgroundColor: '#1a1a1a',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <div
            style={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              textAlign: 'center',
              color: 'white',
            }}
          >
            <div style={{ fontSize: '64px', marginBottom: '20px' }}>▶️</div>
            <h2 style={{ margin: '10px 0' }}>Now Playing</h2>
            <h3 style={{ margin: '10px 0', color: '#ccc' }}>{title}</h3>
            {duration && (
              <p style={{ color: '#999', fontSize: '14px' }}>
                Duration: {duration} minutes
              </p>
            )}
            <p style={{ color: '#666', fontSize: '12px', marginTop: '20px' }}>
              (Mock player - no actual video)
            </p>
          </div>
        </div>
        
        <div
          style={{
            padding: '15px 20px',
            backgroundColor: '#222',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
            <button
              style={{
                background: 'none',
                border: 'none',
                color: 'white',
                fontSize: '24px',
                cursor: 'pointer',
              }}
            >
              ⏸️
            </button>
            <button
              style={{
                background: 'none',
                border: 'none',
                color: 'white',
                fontSize: '20px',
                cursor: 'pointer',
              }}
            >
              🔊
            </button>
            <span style={{ color: 'white', fontSize: '14px' }}>0:00 / {duration || '?'}:00</span>
          </div>
          
          <button
            onClick={onClose}
            style={{
              padding: '8px 20px',
              backgroundColor: '#dc3545',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '14px',
            }}
          >
            Close Player
          </button>
        </div>
      </div>
    </div>
  );
};
