package test.runners;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import test.utils.CommonMethods;
import test.utils.Driver;
import test.utils.LogColor;

@RunWith(Cucumber.class)
@CucumberOptions(       //Enables cucumber power
		plugin= {
				"pretty",     //Colorful formatted console output.
				"html:target/default-cucumber-reports/htmlReport.html",  //Generating html report
				"json:target/cucumber.json",  //Creates Json file with all execution data
				"junit:target/cucumber.xml",   //Junit xml report
				"rerun:target/cucumber.txt",  //Failed tracking with feature file data
				"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",  //Extent report integration
				"test.hooks.StepNameListener" //Plugin for logging like step name, duration, behavior, cucumber events etc
				},
		features= "src/test/resources/features/", //Feature file path
		
		glue= {"test.step_definitions", "test.hooks"},  //Tells cucumber where to look for steps and hooks
		dryRun= false,  //Validates steps without executing in browser.
		
		tags= "@login",
		monochrome = false
		
		)

public class test_runner extends CommonMethods{
	
	public static final Logger logger = LogManager.getLogger(test_runner.class);

	@BeforeClass
	public static void globalSetup() {
		logger.info(LogColor.ThinnerPurple+"@BeforeClass-Test_Runner "+ "Running one time" +LogColor.RESET);
		Driver.BrowserSetup();
	}
	
	
	@AfterClass
	public static void tearDown() {
		logger.info(LogColor.ThinnerPurple+"@AfterClass-Test_Runner "+ "Running one time" +LogColor.RESET);
		logger.info("Closing the Driver");
        try {
            Driver.closeDriver();
        } catch (Exception e) {
            logger.info("Connection reset handled");
        }

        logger.info("Driver Closed");
	}
}
