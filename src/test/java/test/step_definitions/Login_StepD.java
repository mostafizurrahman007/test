package test.step_definitions;

import org.apache.logging.log4j.*;

import io.cucumber.java.en.*;
import test.utils.CommonMethods;
import test.utils.SftAssert;

public class Login_StepD extends CommonMethods{
	public static final Logger logger = LogManager.getLogger(Login_StepD.class);

	@Given("Validate User landed on homepage")
	public void validate_user_landed_on_homepage() {
		logger.info("Verify title");
		
        boolean titleMatched = homePage_pom.verify_homepage_title();
		
 		logger.info("Performing Assertion");
	    softAssert.softAssertTrue(titleMatched, "Title Matched Successfully", "Title didn't Matched");
	   
	}

	@Then("Verify UserID text is visible")
	public void verify_user_id_text_is_visible() {
		logger.info("user_id is visible");
		
		boolean userIdVisiblity = homePage_pom.verify_userid_isvisible();
		
		logger.info("Assertion");
		softAssert.softAssertTrue(userIdVisiblity, "User id is visible in the screen", "User id is not visible in the screen");
		
	}
	
	@Then("Verify Submit is visible")
	public void verify_submit_is_visible() {
		logger.info("Verifying submit button text");
		
		boolean submitBtnVisiblity = homePage_pom.verify_submit_is_isvisible();
		logger.info("Submit button is visible");
		
		logger.info("Assertion");
		softAssert.softAssertTrue(submitBtnVisiblity, "Submit button is visible in the screen", "Submit button is not visible in the screen");
	    
	}
	
	
	@When("Click on Selenium Drop down from the top")
	public void click_on_selenium_drop_down_from_the_top() {
		logger.info("Verifying if clicking on selenium drop down or not");
		
		boolean selenium_dropDown = homePage_pom.verify_Click_on_Selenium_dropDown();
		logger.info("Clicked on Selenium Button");
		
		logger.info("Assertion");
		softAssert.softAssertTrue(selenium_dropDown, "Selenium Dropdown clicking and Showing Table", "Selenium Dropdown not clicking and Showing Table");
	}
	
	
	@Then("Verify  Table Demo is available Under Selenium Drop down")
	public void verify_table_demo_is_available_under_selenium_drop_down() {
		logger.info("Verifying Table Demo button text");
		
		boolean table_demo = homePage_pom.verify_table_demo_is_visible();
		logger.info("Table Demo button is visible");
		
		logger.info("Assertion");
		softAssert.softAssertTrue(table_demo, "Table Demo button displayed successfully", "Table Demo button is not Visible");
	}
}
