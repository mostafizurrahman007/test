@login
Feature: Testing Homepage Items and Login related test cases.

  Background: 
    Given Validate User landed on homepage

  @TC_0001
  Scenario: Verify user landed on Homepage and UserID is available
    Then Verify UserID text is visible

  @TC_0003
  Scenario: Verify  Submit is available
    Then Verify Submit is visible

  @TC_0004
  Scenario: Verify  Table Demo is available Under Selenium Drop down
    When Click on Selenium Drop down from the top
    Then Verify  Table Demo is available Under Selenium Drop down
