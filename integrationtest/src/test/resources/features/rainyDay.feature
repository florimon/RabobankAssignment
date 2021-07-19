Feature: Run 'rainy day'/negative integration tests

  Background:
    * configure headers = { 'Content-Type' : 'application/json' }
    * url serverUrl

  Scenario: Try to create a Power of Attorney for an unknown account
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountNumber: 'RABO 002', accountType: 'PAYMENT', authorizationType: 'READ'}
    When method POST
    Then status 400
    And match $ == {status: 400, error: 'Bad Request', message: "No Payment account with number 'RABO 002' for holder 'testHolder'"}

  Scenario: Try to create a Power of Attorney with a missing AuthorizationType
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountNumber: 'RABO 002', accountType: 'PAYMENT'}
    When method POST
    Then status 400
    And match $ == {status: 400, error: 'Bad Request', message: "Invalid/missing value for property(s): authorizationType"}

  Scenario: Try to create a Power of Attorney with a missing AccountType
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountNumber: 'RABO 002', authorizationType: 'READ'}
    When method POST
    Then status 400
    And match $ == {status: 400, error: 'Bad Request', message: "Invalid/missing value for property(s): accountType"}

  Scenario: Try to create a Power of Attorney with a missing AccountNumber
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountType: 'PAYMENT', authorizationType: 'READ'}
    When method POST
    Then status 400
    And match $ == {status: 400, error: 'Bad Request', message: "Invalid/missing value for property(s): accountNumber"}

  Scenario: Try to create a Power of Attorney with a missing Grantee
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {accountNumber: 'RABO 002', accountType: 'PAYMENT', authorizationType: 'READ'}
    When method POST
    Then status 400
    And match $ == {status: 400, error: 'Bad Request', message: "Invalid/missing value for property(s): grantee"}

  Scenario: Try to create a Power of Attorney with a missing X-UserId header
    Given path '/power-of-attorney'
    And request {grantee: 'testUser', accountNumber: 'RABO 002', accountType: 'PAYMENT', authorizationType: 'READ'}
    When method POST
    Then status 401
    And match $ == {status: 401, error: 'Unauthorized', message: "Required request header 'X-UserId' for method parameter type String is not present"}

  Scenario: Try to list Power of Attorneys with a missing X-UserId header
    Given path '/power-of-attorney'
    When method GET
    Then status 401
    And match $ == {status: 401, error: 'Unauthorized', message: "Required request header 'X-UserId' for method parameter type String is not present"}
    