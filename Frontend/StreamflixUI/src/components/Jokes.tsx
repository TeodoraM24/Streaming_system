import React, { useEffect, useState } from 'react';

interface JokeResponse {
    error: boolean;
    category: string;
    type: string;
    setup: string;
    delivery: string;
}

export const Jokes: React.FC = () => {
    const [joke, setJoke] = useState<JokeResponse | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const loadRandomJoke = async () => {
        setLoading(true);
        setError('');

        try {
            const response = await fetch('https://v2.jokeapi.dev/joke/Programming?type=twopart');

            if (!response.ok) {
                throw new Error('Failed to load joke');
            }

            const data = await response.json();

            if (data.error) {
                throw new Error('JokeAPI returned an error');
            }

            setJoke(data);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Something went wrong');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadRandomJoke();
    }, []);

    return (
        <div
            style={{
                minHeight: '70vh',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center'
            }}
        >
            <div style={{ width: '100%', maxWidth: '700px' }}>
                <div
                    className="sf-page-header"
                    style={{
                        display: 'block',
                        textAlign: 'left',
                        marginBottom: '24px'
                    }}
                >
                    <h2 className="sf-page-title">😂 Programming Jokes</h2>
                    <p style={{ color: 'var(--text-muted)', marginTop: '6px' }}>
                        Generate a random programming joke from JokeAPI
                    </p>
                </div>

                {error && <div className="sf-alert sf-alert-error">{error}</div>}

                {joke && (
                    <div className="sf-card" style={{ width: '100%', marginBottom: '24px', textAlign: 'left' }}>
                        <div style={{ marginBottom: '16px' }}>
                            <span className="sf-badge sf-badge-primary">
                                {joke.category}
                            </span>
                        </div>

                        <div style={{ marginBottom: '18px' }}>
                            <span className="sf-label">Setup</span>
                            <p style={{ fontSize: '18px', color: 'var(--text)' }}>
                                {joke.setup}
                            </p>
                        </div>

                        <div>
                            <span className="sf-label">Delivery</span>
                            <p style={{ fontSize: '18px', color: 'var(--accent-light)', fontWeight: 600 }}>
                                {joke.delivery}
                            </p>
                        </div>
                    </div>
                )}

                <div style={{ display: 'flex', justifyContent: 'center' }}>
                    <button
                        onClick={loadRandomJoke}
                        disabled={loading}
                        className="sf-btn sf-btn-primary"
                    >
                        {loading ? 'Loading...' : 'Generate Joke'}
                    </button>
                </div>
            </div>
        </div>
    );
};