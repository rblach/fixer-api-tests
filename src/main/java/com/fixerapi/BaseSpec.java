package com.fixerapi;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseSpec {

    CustomLogFilter filter = new CustomLogFilter();

    public RequestSpecBuilder getRequestSpecBuilder(String baseUri) {
        return new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .addFilter(filter)
            .addFilter(new ResponseLoggingFilter())
            .addFilter(new RequestLoggingFilter());
    }

    public RequestSpecification buildRequestSpecification(String baseUri) {
        RequestSpecBuilder requestSpecBuilder = getRequestSpecBuilder(baseUri)
            .setRelaxedHTTPSValidation()
            .setContentType(ContentType.JSON)
            .setUrlEncodingEnabled(false);

        return requestSpecBuilder.build();
    }
}
