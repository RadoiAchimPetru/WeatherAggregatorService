Feature: Weather Aggregator Service
  As a user of the weather aggregator
  I want to fetch and retrieve weather readings
  So that I can track weather conditions for cities

  Background:
    Given the Open-Meteo API is stubbed for "Timisoara"

  Scenario: Successfully fetch weather for a city
    When I POST to "/weather/fetch?city=Timisoara"
    Then the response status is 201
    And the response contains "Timisoara"
    And the response contains a temperature field

  Scenario: Retrieve all stored readings for a city
    Given I have previously fetched weather for "Timisoara"
    When I GET "/weather/Timisoara"
    Then the response status is 200
    And the response is a list with at least 1 reading

  Scenario: City not found returns 404
    Given the Open-Meteo API returns no results for "CiudadInventada"
    When I POST to "/weather/fetch?city=CiudadInventada"
    Then the response status is 404
    And the response contains "not found"