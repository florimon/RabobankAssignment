# Note: the recommended approach is to have Scenario's be independent of each other, so that they can be run in
# parallel. For the sake of simplicity, here parallel running is turned off so that the scenario's can build on
# each other.
@parallel=false
Feature: Run 'happy flow' integration tests

  Background:
    * configure headers = { 'Content-Type' : 'application/json' }
    * url serverUrl

  Scenario: Create a Power of Attorney for an existing Payment account, authorization READ
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountNumber: 'RABO 001', accountType: 'PAYMENT', authorizationType: 'READ'}
    When method POST
    Then status 200
    And match $ contains {grantee: 'testUser', accountNumber: 'RABO 001', accountHolder: 'testHolder', accountType: 'PAYMENT', authorizationType: 'READ'}

  Scenario: List the Power of Attorneys for 'testUser' that just got one granted
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testUser'}
    When method GET
    Then status 200
    And match $.powerOfAttorneys == [{grantee: 'testUser', accountNumber: 'RABO 001', accountHolder: 'testHolder', accountType: 'PAYMENT', authorizationType: 'READ'}]
    
  Scenario: List the Power of Attorneys for 'otheruser' that has none granted
    Given path '/power-of-attorney'
    * headers {X-UserId: 'otherUser'}
    When method GET
    Then status 200
    And match $.powerOfAttorneys == []

  Scenario: Update an existing Power of Attorney to authorization WRITE
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountNumber: 'RABO 001', accountType: 'PAYMENT', authorizationType: 'WRITE'}
    When method POST
    Then status 200

  Scenario: Verify that the existing Power of Attorney was updated instead of a new one created
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testUser'}
    When method GET
    Then status 200
    And match $.powerOfAttorneys == [{grantee: 'testUser', accountNumber: 'RABO 001', accountHolder: 'testHolder', accountType: 'PAYMENT', authorizationType: 'WRITE'}]

  Scenario: Create a Power of Attorney for an existing Savings account, authorization READ
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testHolder'}
    And request {grantee: 'testUser', accountNumber: 'RABO 001', accountType: 'SAVINGS', authorizationType: 'READ'}
    When method POST
    Then status 200
    And match $ contains {grantee: 'testUser', accountNumber: 'RABO 001', accountHolder: 'testHolder', accountType: 'SAVINGS', authorizationType: 'READ'}

  Scenario: List the Power of Attorneys for 'testUser' and verify that there should now be two
    Given path '/power-of-attorney'
    * headers {X-UserId: 'testUser'}
    When method GET
    Then status 200
    And match $.powerOfAttorneys contains {grantee: 'testUser', accountNumber: 'RABO 001', accountHolder: 'testHolder', accountType: 'SAVINGS', authorizationType: 'READ'}
    And match $.powerOfAttorneys contains {grantee: 'testUser', accountNumber: 'RABO 001', accountHolder: 'testHolder', accountType: 'PAYMENT', authorizationType: 'WRITE'}



