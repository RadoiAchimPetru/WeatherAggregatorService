package com.weather.adapter.http;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.weather.adapter.out.http.OpenMeteoAdapter;
import com.weather.domain.model.WeatherReading;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "OpenMeteoProvider", port = "8090")
class OpenMeteoContractTest {

    @Pact(consumer = "WeatherAggregator")
    public RequestResponsePact geocodingPact(PactDslWithProvider builder) {
        return builder
            .given("Timisoara exists")
            .uponReceiving("a geocoding request for Timisoara")
            .path("/v1/search")
            .query("name=Timisoara&count=1&language=en&format=json")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body("""
                {
                  "results": [{
                    "latitude": 45.75,
                    "longitude": 21.23,
                    "name": "Timisoara",
                    "country": "Romania"
                  }]
                }
            """)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "geocodingPact")
    void whenGeocodingTimisoara_shouldReturnCoordinates(MockServer mockServer) {
        
        OpenMeteoAdapter adapter = new OpenMeteoAdapter();
        adapter.geocodingUrl = mockServer.getUrl() + "/v1/search";
        adapter.weatherUrl   = mockServer.getUrl() + "/v1/forecast";

       
        assertDoesNotThrow(() -> {
            adapter.fetchCurrentWeather("Timisoara");
           
        });
    }
}