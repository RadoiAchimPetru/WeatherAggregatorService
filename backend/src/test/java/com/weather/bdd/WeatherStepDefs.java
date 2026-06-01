package com.weather.bdd;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import com.github.tomakehurst.wiremock.client.WireMock; // IMPORT IMPORTANT
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class WeatherStepDefs {

    private Response lastResponse;

    @Given("the Open-Meteo API is stubbed for {string}")
    public void stubOpenMeteoForCity(String city) {
        WireMockResource.getWireMockServer().stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/v1/search"))
                        .withQueryParam("name", WireMock.equalTo(city))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"results\":[{" +
                                        "\"latitude\":45.75," +
                                        "\"longitude\":21.23," +
                                        "\"name\":\"" + city + "\"," +
                                        "\"country\":\"Romania\"}]}")));
    }

    @Given("the Open-Meteo API returns no results for {string}")
    public void stubNoResultsForCity(String city) {
        WireMockResource.getWireMockServer().stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/v1/search"))
                        .withQueryParam("name", WireMock.equalTo(city))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"results\":[]}")));
    }

    @Given("I have previously fetched weather for {string}")
    public void previouslyFetchedWeather(String city) {
        given()
                .contentType(ContentType.JSON) // ADĂUGAT
                .when()
                .post("/weather/fetch?city=" + city)
                .then()
                .statusCode(201);
    }

    @When("I POST to {string}")
    public void iPostTo(String path) {
        lastResponse = given()
                .contentType(ContentType.JSON) // ADĂUGAT
                .when()
                .post(path);
    }

    @When("I GET {string}")
    public void iGetPath(String path) {
        lastResponse = given()
                .when()
                .get(path);
    }

    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) {
        Assertions.assertEquals(expectedStatus, lastResponse.getStatusCode());
    }

    @Then("the response contains {string}")
    public void responseContains(String text) {

        try {
            lastResponse.then().body("error", containsString(text));
        } catch (AssertionError e) {

            lastResponse.then().body(containsString(text));
        }
    }

    @Then("the response contains a temperature field")
    public void responseContainsTemperature() {
        lastResponse.then().body("temperature", notNullValue());
    }

    @Then("the response is a list with at least {int} reading")
    public void responseIsListWithAtLeast(int minSize) {
        lastResponse.then().body("size()", greaterThanOrEqualTo(minSize));
    }
}