package com.weather.bdd;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class WeatherStepDefs {

    private Response lastResponse;

    @Given("the Open-Meteo API is stubbed for {string}")
    public void stubOpenMeteoForCity(String city) {
        
    }

    @When("I POST to {string}")
    public void iPostTo(String path) {
        lastResponse = given()
            .when()
            .post("http://localhost:8081" + path);
    }

    @When("I GET {string}")
    public void iGetPath(String path) {
        lastResponse = given()
            .when()
            .get("http://localhost:8081" + path);
    }

    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) {
        Assertions.assertEquals(expectedStatus, lastResponse.getStatusCode());
    }

    @Then("the response contains {string}")
    public void responseContains(String text) {
        lastResponse.then().body(containsString(text));
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