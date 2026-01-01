package test.step_definitions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.cucumber.java.en.*;
import test.utils.CommonMethods;

public class Test_sd extends CommonMethods{
	public static final Logger logger = LogManager.getLogger(Test_sd.class);
	
	@Given("User is on homepage")  //Gherkins keyword
	public void user_is_on_homepage() {
		logger.info("Step Difinition");
		waitFor(1);
		
	}
}
