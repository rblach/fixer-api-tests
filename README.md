# Fixer API Timeseries endpoint Tests

## Overview

This project contains automated tests for the Fixer API timeseries endpoint using RestAssured and Cucumber in Java.

## Prerequisites

- Java 17 or higher
- Maven

## Setup

1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/fixer-api-tests.git
   ```
2. Navigate to the project directory: ```cd fixerApiTests```
3. Create and update the config.properties file in src/test/resources with your API keys:
    ```
    valid_api_key=YOUR_VALID_API_KEY
    invalid_api_key=INVALID_API_KEY
    rate_limit_exceeded_api_key=RATE_LIMIT_EXCEEDED_API_KEY
    ```
   Note: you can use already delivered invalid and rate_limit api keys, you just need to provide valid one

## Running tests

To run the tests, use the following Maven command:
``` mvn clean test```

## Reporting
Test reports will be generated in the target/cucumber-reports.html file.
