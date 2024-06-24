Feature: Fixer API Timeseries Endpoint

  Scenario Outline: Valid request with different query parameters
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | <start_date> |
      | end_date   | <end_date>   |
      | base       | <base>       |
      | symbols    | <symbols>    |
    Then the response status code should be 200
    And the response should contain timeseries data for the given date range
      | start_date | <start_date> |
      | end_date   | <end_date>   |
    And the response should contain exchange rates
      | from | <base>    |
      | to   | <symbols> |

    Examples:
      | start_date | end_date   | base | symbols |
      | 2023-01-01 | 2023-01-31 | USD  | EUR,GBP |
      | 2023-01-01 | 2023-01-15 | USD  | EUR     |
      | 2023-01-15 | 2023-01-31 | EUR  | USD,GBP |
      | 2023-01-01 | 2023-01-31 | GBP  | USD,EUR |

  Scenario: Valid request with only required params
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | 2022-01-01 |
      | end_date   | 2022-02-02 |
    Then the response status code should be 200
    And the response should contain timeseries data for the given date range
      | start_date | 2022-01-01 |
      | end_date   | 2022-02-02 |

  Scenario: Exceeding maximum timeframe of 365 days for date range
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | 2022-01-01 |
      | end_date   | 2024-01-01 |
    Then the response status code should be 200
    And the response contains error with code 505 and type "time_frame_too_long"

  Scenario Outline: Retrieving results for invalid date range (future/past)
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | <start_date> |
      | end_date   | <end_date>   |
    Then the response status code should be 200
    And the response contains error with code 106 and type "no_rates_available"

    Examples:
      | start_date | end_date   |
      | 2025-01-01 | 2025-01-02 |
      | 1901-12-13 | 1901-12-25 |

  Scenario: Request with missing required parameters
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | <start_date> |
    Then the response status code should be 400

  Scenario: Request with invalid API key
    Given I have an invalid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | 2023-01-01 |
      | end_date   | 2023-01-31 |
    Then the response status code should be 401

  Scenario: Request with missing API key
    Given I do not provide an API key
    When I send a GET request to the timeseries endpoint without API key
    Then the response status code should be 401

# Returns 200 OK but with error code 302 because of invalid date??? If endpoint doesn't exist it should return 404
  Scenario: Requesting non-existent endpoint
    Given I have a valid API key
    When I send a GET request to a non-existent endpoint
    Then the response status code should be 200
    And the response contains error with code 302 and type "invalid_date"

  Scenario: Exceeding rate limit
    Given I have exceeded the API rate limit
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | 2023-01-01 |
      | end_date   | 2023-01-31 |
    Then the response status code should be 429

#  Invalid request parameters return 200 OK with 501 error code in message because of "no_timeframe_supplied" but
#  IMO it should return 400 immediately, as per documentation "Codes in the 2xx range indicate success"
  Scenario: Invalid request parameters
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | invalid_param | invalid_param |
    Then the response status code should be 200
    And the response contains error with code 501 and type "no_timeframe_supplied"

# Returns 200 with error code 202 because of "invalid_currency_codes". IMO it should return 400 because currencies codes
# are probably a enum so if there is no such currency code it should be treated as Bad Request sent
  Scenario Outline: Invalid currency parameters
    Given I have a valid API key
    When I send a GET request to the timeseries endpoint with the following parameters:
      | start_date | 2023-01-01 |
      | end_date   | 2023-01-31 |
      | base       | <base>     |
      | symbols    | <symbols>  |
    Then the response status code should be 200
    Then the response status code should be 200
    And the response contains error with code <errorCode> and type "<errorType>"

    Examples:
      | base | symbols | errorCode | errorType              |
      | EUR  | XXX     | 202       | invalid_currency_codes |
      | XXX  | USD     | 201       | invalid_base_currency  |
      | ABC  | XXX     | 201       | invalid_base_currency  |
