@homepage
Feature: Testing Homepage Items and Login related test cases
  I want to use this template to validate homepage

  @tag1
  Scenario: Homepage Validation
    Given User is on Homepage

  @tag2
  Scenario: Verify clicking on Today's Deals button
    Given User is on homepage 2
    When User clicks on Today's Deals
