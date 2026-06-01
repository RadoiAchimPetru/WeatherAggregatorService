
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios';
import WeatherApp from './WeatherApp';

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

test('fetch button calls API and displays results', async () => {
    
    mockedAxios.post.mockResolvedValueOnce({ data: {} });
    mockedAxios.get.mockResolvedValueOnce({
        data: [{
            id: 1, city: 'Timisoara',
            temperature: 22.4, windSpeed: 14.2,
            weatherDescription: 'Overcast',
            fetchedAt: new Date().toISOString()
        }]
    });

    render(<WeatherApp />);

    // Completăm câmpul și apăsăm butonul
    fireEvent.change(screen.getByTestId('city-input'), {
        target: { value: 'Timisoara' }
    });
    fireEvent.click(screen.getByTestId('fetch-button'));

    // Așteptăm să apară tabelul
    await waitFor(() => {
    expect(screen.getByTestId('readings-table')).toBeInTheDocument();
});
expect(screen.getByText('Overcast')).toBeInTheDocument();
});