package com.fixerapi.clients;

import com.fixerapi.BaseSpec;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class FixerApiClient extends BaseSpec {
    public FixerApiClient(String apiKey) {
        this.apiKey = apiKey;
    }

    private final String FIXER_API_URL = "https://api.apilayer.com/fixer";
    RequestSpecification spec = buildRequestSpecification(FIXER_API_URL);
    private final String apiKey;

    public Response getTimeseries(Map<String, String> queryParams) {
        var request = given()
            .spec(spec)
            .header("apikey", this.apiKey);

        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            request.queryParam(param.getKey(), param.getValue());
        }

        return request.when().get("/timeseries");
    }

    public Response getTimeseriesWithoutApiKey() {
        var request = given()
            .spec(spec);

        return request.when().get("/timeseries");
    }

    public Response getNonExistingEndpoint() {
        var request = given()
            .spec(spec)
            .header("apikey", this.apiKey);

        return request.when().get("/non-existing");
    }
}
