package com.weather.bdd;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


@QuarkusTest
@QuarkusTestResource(WireMockResource.class)
public class CucumberRunnerTest {

    @Test
    public void runCucumberScenarios() throws Exception {
        byte exitCode = io.cucumber.core.cli.Main.run(
                new String[]{
                        "--glue", "com.weather.bdd",
                        "--plugin", "pretty",
                        "--plugin", "html:target/cucumber-report.html",
                        "classpath:features"
                },
                Thread.currentThread().getContextClassLoader()
        );

        assertEquals(0, exitCode,
                "Cucumber scenarios FAILED! Verifică target/cucumber-report.html");
    }
}