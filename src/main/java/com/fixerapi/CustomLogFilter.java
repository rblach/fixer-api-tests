package com.fixerapi;

import io.restassured.authentication.PreemptiveBasicAuthScheme;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class CustomLogFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        String requestBuilder = createCurl(requestSpec);

        TestContext.remember("request.log", requestBuilder);
        String responseBuilder = response.getStatusLine().concat("\n").concat(response.getBody().asString());
        TestContext.remember("response.log", responseBuilder);
        return response;
    }

    private String createCurl(FilterableRequestSpecification requestSpec) {
        StringBuilder curl = new StringBuilder(String.format("%ncurl --location --request %s %s \\%n", requestSpec.getMethod(), requestSpec.getURI()));

        if (requestSpec.getHeaders().size() > 1) {
            requestSpec.getHeaders().asList().forEach(h -> curl.append(String.format("--header '%s' \\%n",
                h.toString().replaceFirst("=", ":"))));
        }

        if (requestSpec.getProxySpecification() != null) {
            curl.append(String.format("--proxy %s \\%n", requestSpec.getProxySpecification().toString()));
        }

        if (requestSpec.getAuthenticationScheme() instanceof PreemptiveBasicAuthScheme) {
            curl.append(
                String.format(
                    "--header 'Authorization: %s' \\%n",
                    ((PreemptiveBasicAuthScheme) (requestSpec.getAuthenticationScheme())).generateAuthToken()
                )
            );
        }

        if (!requestSpec.getFormParams().isEmpty()) {
            String contentTypeValue = requestSpec.getHeaders().getValue("Content-Type");
            if (contentTypeValue != null && contentTypeValue.contains("application/x-www-form-urlencoded")) {
                requestSpec.getFormParams().forEach((key, value) -> curl.append(String.format("--data-urlencode '%s=%s' \\%n", key, value)));
            } else {
                requestSpec.getFormParams().forEach((key, value) -> curl.append(String.format("--form '%s'='%s' \\%n", key, value)));
            }
        }

        if (requestSpec.getBody() != null && !requestSpec.getBody().toString().isBlank() && !requestSpec.getBody().equals("null")) {
            curl.append(String.format("--data-raw '%s'", requestSpec.getBody().toString()));
        }
        return curl.toString();
    }
}