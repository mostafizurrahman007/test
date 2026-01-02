package test.pages;

import org.apache.logging.log4j.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;

import test.utils.CommonMethods;
import test.utils.LogColor;

public class HomePage_POM extends CommonMethods{
	public static final Logger logger = LogManager.getLogger(HomePage_POM.class);

	//@FindBy
	@FindBy(xpath = "//td[normalize-space()='Email ID' and @align='right']")
			public WebElement userid_text;
	
			
			 
	//By Locator
	By user_id_text_By = By.xpath("//td[normalize-space()='Email ID' and @align='right']");
	By submit_btn_text = By.xpath("//*[@type='submit']");
	By table_demo_text = By.xpath("//a[normalize-space()='Table Demo']");
			
			
	public boolean verify_homepage_title() {
		
		try {
			logger.info("Getting the actual title");
			String actualTitle = driver.getTitle();
			logger.info("Got the title");
			String expectedTitle = "Guru99 Bank Home Page";
			
			if(actualTitle.equals(expectedTitle)) {
				return true;
			}
			else {
				return false;
			}
			
		} catch (Exception e) {
			logger.error(LogColor.RED+ e+ LogColor.RESET);
			return false;
		}
	}
	
	public boolean verify_userid_isvisible() {
		
		try {
			//boolean presence = isElementDisplayed(userid_text);
			boolean presence = isElementPresent(user_id_text_By);
			if(presence) {
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			logger.error(LogColor.RED+ e+ LogColor.RESET);
			return false;
		}
	}
	
	public boolean verify_submit_is_isvisible() {
		try {
			boolean submit_btn_text_by = isElementPresent(submit_btn_text);
			if(submit_btn_text_by) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e) {
			logger.error(LogColor.RED+ e+ LogColor.RESET);
			return false;
		}
	}
	
	public boolean verify_Click_on_Selenium_dropDown() {
		WebElement selenium_dropdown = driver.findElement(By.xpath("//a[normalize-space()='Selenium']"));
		try {
			clickAndDraw(selenium_dropdown);
			return true;
			
		} catch (Exception e) {
			logger.error(LogColor.RED+ e+ LogColor.RESET);
			return false;
		}
		
	}
	
	public boolean verify_table_demo_is_visible() {
		try {
			boolean tableDemo = isElementPresent(table_demo_text);
			if(tableDemo) {
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			logger.error(LogColor.RED+ e+ LogColor.RESET);
			return false;
		}
	}
}
