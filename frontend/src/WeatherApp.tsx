import { useState } from 'react';
import axios from 'axios';

interface WeatherReading {
    id: number;
    city: string;
    temperature: number;
    windSpeed: number;
    weatherDescription: string;
    fetchedAt: string;
}

export default function WeatherApp() {
    const [city, setCity] = useState('');
    const [readings, setReadings] = useState<WeatherReading[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchWeather = async () => {
        if (!city.trim()) return;
        setLoading(true);
        setError(null);

        try {
            
            await axios.post(`/weather/fetch?city=${city}`);

            
            const { data } = await axios.get<WeatherReading[]>(`/weather/${city}`);
            setReadings(data);
        } catch (err: any) {
            setError(err.response?.data?.error || 'A apărut o eroare');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 600, margin: '40px auto', fontFamily: 'sans-serif' }}>
            <h1>Weather Aggregator</h1>

            <div style={{ display: 'flex', gap: 8, marginBottom: 24 }}>
                <input
                    value={city}
                    onChange={e => setCity(e.target.value)}
                    placeholder="Ex: Timisoara"
                    data-testid="city-input"
                    style={{ flex: 1, padding: 8, fontSize: 16 }}
                />
                <button
                    onClick={fetchWeather}
                    disabled={loading}
                    data-testid="fetch-button"
                    style={{ padding: '8px 16px', fontSize: 16 }}
                >
                    {loading ? 'Loading...' : 'Fetch Weather'}
                </button>
            </div>

            {error && (
                <p style={{ color: 'red' }} data-testid="error-message">{error}</p>
            )}

            {readings.length > 0 && (
                <table style={{ width: '100%', borderCollapse: 'collapse' }}
                    data-testid="readings-table">
                    <thead>
                        <tr style={{ background: '#f0f0f0' }}>
                            <th style={{ padding: 8, textAlign: 'left' }}>Temp (°C)</th>
                            <th style={{ padding: 8, textAlign: 'left' }}>Wind (km/h)</th>
                            <th style={{ padding: 8, textAlign: 'left' }}>Condition</th>
                            <th style={{ padding: 8, textAlign: 'left' }}>Time</th>
                        </tr>
                    </thead>
                    <tbody>
                        {readings.map(r => (
                            <tr key={r.id} style={{ borderBottom: '1px solid #ddd' }}>
                                <td style={{ padding: 8 }}>{r.temperature}</td>
                                <td style={{ padding: 8 }}>{r.windSpeed}</td>
                                <td style={{ padding: 8 }}>{r.weatherDescription}</td>
                                <td style={{ padding: 8 }}>
                                    {new Date(r.fetchedAt).toLocaleTimeString('ro-RO')}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}