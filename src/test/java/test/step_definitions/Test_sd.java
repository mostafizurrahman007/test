package test.step_definitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import io.cucumber.java.en.*;
import test.utils.CommonMethods;

public class Test_sd extends CommonMethods{
	public static final Logger logger = LogManager.getLogger(Test_sd.class);
	
	@Given("User is on Homepage")
	public void user_is_on_homepage() {
		logger.info("User is on homepage");
	}
	
	@Given("User is on homepage 2")
	public void user_is_on_homepage2() {
		logger.info("User is on homepage");
	}
	
	@When("User clicks on Today's Deals")
	public void verify_clicking() {
		logger.info("User is clicking");
	}
	
}
