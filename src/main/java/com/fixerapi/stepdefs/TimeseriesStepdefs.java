package com.fixerapi.stepdefs;

import com.fixerapi.clients.FixerApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class TimeseriesStepdefs {

    private final Properties properties;
    private FixerApiClient apiClient;
    private Response response;
    private String apiKey;

    public TimeseriesStepdefs() {
        properties = new Properties();
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                System.out.println("config.properties not found in classpath");
            } else {
                properties.load(input);
                System.out.println("config.properties loaded successfully");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Given("I have a valid API key")
    public void i_have_a_valid_api_key() {
        apiKey = properties.getProperty("valid_api_key");
        apiClient = new FixerApiClient(apiKey);
    }

    @Given("I have an invalid API key")
    public void i_have_an_invalid_api_key() {
        apiKey = properties.getProperty("invalid_api_key");
        apiClient = new FixerApiClient(apiKey);
    }

    @Given("I do not provide an API key")
    public void i_do_not_provide_an_api_key() {
        apiKey = "";
        apiClient = new FixerApiClient(apiKey);
    }

    @Given("I have exceeded the API rate limit")
    public void i_have_exceeded_the_api_rate_limit() {
        apiKey = properties.getProperty("rate_limit_exceeded_api_key");
        apiClient = new FixerApiClient(apiKey);
    }

    @When("I send a GET request to the timeseries endpoint with the following parameters:")
    public void i_send_a_get_request_to_the_timeseries_endpoint_with_the_following_parameters(Map<String, String> queryParams) {
        response = apiClient.getTimeseries(queryParams);
    }

    @When("I send a GET request to the timeseries endpoint without API key")
    public void i_send_a_get_request_to_the_timeseries_endpoint() {
        response = apiClient.getTimeseriesWithoutApiKey();
    }

    @When("I send a GET request to a non-existent endpoint")
    public void i_send_a_get_request_to_a_non_existent_endpoint() {
        response = apiClient.getNonExistingEndpoint();
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int statusCode) {
        int responseStatusCode = response.getStatusCode();
        Assertions.assertThat(responseStatusCode)
            .withFailMessage("Actual status code is not as expected. Actual: %s\nExpected: %s",
                responseStatusCode, statusCode)
            .isEqualTo(statusCode);
    }

    @Then("the response contains error with code {int} and type {string}")
    public void the_response_contains_error_with_code_and_message(int errorCode, String type) {
        Map<String, Object> errorMap = response.jsonPath().getMap("error");

        Object actualErrorCode = errorMap.get("code");
        Assertions.assertThat(actualErrorCode)
            .withFailMessage("Message error code is not as expected. Actual: %s\nExpected: %s",
                actualErrorCode, errorCode)
            .isEqualTo(errorCode);

        Object actualErrorType = errorMap.get("type");
        Assertions.assertThat(actualErrorType)
            .withFailMessage("Message type value is not as expected. Actual: %s\nExpected: %s",
                actualErrorType, type)
            .isEqualTo(type);
    }

    @Then("the response should contain timeseries data for the given date range")
    public void the_response_should_contain_timeseries_data_for_the_given_date_range(Map<String, String> dateRange) {
        String startDate = dateRange.get("start_date");
        String endDate = dateRange.get("end_date");

        String actualStartDate = response.jsonPath().getString("start_date");
        Assertions.assertThat(actualStartDate)
            .withFailMessage("Actual start date is not as expected. Actual: %s\nExpected: %s",
                actualStartDate, startDate)
            .isEqualTo(startDate);

        String actualEndDate = response.jsonPath().getString("end_date");
        Assertions.assertThat(actualEndDate)
            .withFailMessage("Actual end date is not as expected. Actual: %s\nExpected: %s",
                actualEndDate, endDate)
            .isEqualTo(endDate);
    }

    @Then("the response should contain exchange rates")
    public void the_response_should_contain_exchange_rates(Map<String, String> exchangeRates) {
        String base = exchangeRates.get("from");
        String symbols = exchangeRates.get("to");

        String[] symbolsArray = symbols.split(",");

        String actualBaseCurrency = response.jsonPath().getString("base");
        Assertions.assertThat(actualBaseCurrency)
            .withFailMessage("Base currency is not as expected. Actual: %s\nExpected: %s",
                actualBaseCurrency, base)
            .isEqualTo(base);

        Map<String, Map<String, Object>> rates = response.jsonPath().getMap("rates");

        for (Map.Entry<String, Map<String, Object>> entry : rates.entrySet()) {
            Map<String, Object> dailyRates = entry.getValue();
            Set<String> actualTargetCurrencies = dailyRates.keySet();
            Assertions.assertThat(actualTargetCurrencies)
                .withFailMessage("Target currency is not as expected. Actual: %s\nExpected: %s",
                    actualTargetCurrencies, symbolsArray)
                .contains(symbolsArray);
        }
    }
}
