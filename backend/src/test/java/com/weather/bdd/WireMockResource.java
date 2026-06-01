package com.weather.bdd;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

public class WireMockResource implements QuarkusTestResourceLifecycleManager {


    private static WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {

        wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig().port(8089)
        );
        wireMockServer.start();

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/v1/search"))
                .withQueryParam("name", WireMock.equalTo("Timisoara"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"results\":[{" +
                                        "\"latitude\":45.75," +
                                        "\"longitude\":21.23," +
                                        "\"name\":\"Timisoara\"," +
                                        "\"country\":\"Romania\"}]}"
                        )
                )
        );


        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/v1/search"))
                .withQueryParam("name", WireMock.equalTo("CiudadInventada"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"results\":[]}")
                )
        );


        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/v1/forecast"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                                .withBody("{\"current\": {\"temperature_2m\": 21.0, \"wind_speed_10m\": 6.1, \"weather_code\": 3}}")
                        )

        );


        return Map.of(
                "openmeteo.geocoding.url", "http://localhost:8089/v1/search",
                "openmeteo.weather.url", "http://localhost:8089/v1/forecast"
        );
    }


    public static WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}