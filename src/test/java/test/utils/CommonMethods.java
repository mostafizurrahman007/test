package test.utils;

import java.io.File;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.DayOfWeek;

import java.time.Duration;

import java.time.LocalDate;

import java.time.Month;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.Calendar;

import java.util.Date;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import java.util.Random;

import java.util.Set;

import java.util.TimeZone;

import java.util.concurrent.TimeUnit;

import java.util.stream.IntStream;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;

import org.openqa.selenium.Alert;

import org.openqa.selenium.By;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import org.openqa.selenium.support.ui.ExpectedCondition;

import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import com.google.common.base.Function;

import io.cucumber.core.gherkin.Step;
import io.cucumber.java.Scenario;
import test.pages.HomePage_POM;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

public class CommonMethods extends Driver{  //Extends Drives class properties.
	
	/** ------------------ Global Variables -------------------- **/
	public static WebDriver driver = Driver.getDriver();
	public static final int ELEMENT_WAIT_TIMEOUT_SECONDS = 40;
	public static final int ELEMENT_POLLING_TIME_MILIS = 50; //Waiting for element appearing or visibility.
	public static final int PAGE_LOAD_TIMEOUT_SECONDS = 30;
	public static final int JQUERY_LOAD_TIMEOUT_SECONDS = 30; //Java constant declaration for tracking jQuery loading timeout
	public static final int SESSION_TIMEOUT_MINUTES = 15;
	JavascriptExecutor js = (JavascriptExecutor) driver; //Controlling browser with JavaScript commands.
	WebDriverWait wait = null;
	public static final Logger logger = LogManager.getLogger(CommonMethods.class); // Log configuration for getting log message.
	
	
	// ================================
	// 🔹 Class Objects
	// ================================

	public static SftAssert softAssert; //Soft Assertion declared globally.
	public static ExcelUtil excelUtil = new ExcelUtil(); //Excel reading tool declared globally.


	// ================================
	// 🔹 Page Object Models
	// ================================

	public static HomePage_POM homePage_pom = new HomePage_POM();



  	// ================================
	// 🔹 Different Wait Methods
	// ================================

          
		// Wait for Javascript or JQuery to load
	public static void waitForPageAndAjaxToLoad() { //A shared method that waits for both the webpage AND background requests to fully finish loading.

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(JQUERY_LOAD_TIMEOUT_SECONDS)); // Creating wait for JQuery.
		// Wait for document.readyState to be 'complete'
		wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState")  //Wait until document is in ready state.
				.equals("complete"));
		// Wait for jQuery.active == 0 if jQuery is present
		try {
			Boolean jQueryDefined = (Boolean) ((JavascriptExecutor) driver) 
					.executeScript("return typeof jQuery != 'undefined'"); // Checking JQuery existance using JavaScript
			if (jQueryDefined) {
				wait.until(webDriver -> (Boolean) ((JavascriptExecutor) webDriver)
						.executeScript("return jQuery.active == 0"));  //Waits until all jQuery AJAX requests have completed
			}
		} catch (Exception e) {
			// jQuery not present or error occurred — skip AJAX wait
			System.out.println("jQuery not detected or error occurred. Skipping AJAX wait."); //If JQuery not found then skip wait and go next step.
		}

	}

	// newly implemented for faster response/ wait with locator
	public WebElement waitForElement(By locator) {

		WebElement elementLocator = null;
		removeBorder();  //// Removes visual debug borders
		logger.info("Checking visibility of the Element on browser screen");
		// Prioritize ExpectedConditions for efficiency
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(ELEMENT_WAIT_TIMEOUT_SECONDS));
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated(locator)); //Waiting to DOM existance
			elementLocator = driver.findElement(locator);
		} catch (TimeoutException e) {
			// Handle timeout exception (optional, log or throw as needed)
			logger.warn(
					LogColor.RED + "Element with locator " + locator + " not found within timeout." + LogColor.RESET);
		}
		// Check element visibility and enabled state only if necessary
		if (!elementLocator.isDisplayed() || !elementLocator.isEnabled()) {
			// Consider using a more specific ExpectedCondition or custom implementation
			wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {

					WebElement element = driver.findElement(locator);
					if (element.isDisplayed() && element.isEnabled()) {  // Wait for element to become both visible and enabled
						return element;
					} else {
						return null; 
					}

				}
			});
		}
		// Draw border and flash (consider optimization techniques)
		drawborder(elementLocator); // Assuming `drawBorder` takes WebElement as parameter
		// flash(elementLocator); // Assuming `flash` takes WebElement as parameter
		logger.info("Wait for the element is completed, Element is visible on the screen");
		return elementLocator;

	}

	public WebElement waitForDisableElement(By locator) {

		removeBorder(); // here we are calling removeBorder method. This method is removing any border that was created earliar using java script.
		logger.info("Waiting for the disabled element to be present on the browser screen");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ELEMENT_WAIT_TIMEOUT_SECONDS));
		WebElement elementLocator = null;
		try {
			// Custom ExpectedCondition to wait for a disabled element
			elementLocator = wait.until(driver -> {
				try {
					WebElement element = driver.findElement(locator);
					if (!element.isEnabled()) {
						return element; //Returns the element immediately if it's disabled
					}
				} catch (Exception e) {
					// Element not found or stale, continue waiting
					return null;
				}
				return null;
			});
		} catch (TimeoutException e) {
			logger.warn(LogColor.RED + "Disabled element with locator " + locator + " not found within timeout."
					+ LogColor.RESET); //After colorful log it resets to normal text color for next.
			return null;
		}
		drawborder(elementLocator);
		// flash(elementLocator); // Uncomment if needed
		logger.info("Wait for the disabled element is completed, Element is present and not editable");
		return elementLocator;

	}

	public void waitForPageToLoadfor(int sec) { // Used this method to wait for page load for specific time.

		logger.info("Wait for Web Page to load completely");
		wait = new WebDriverWait(this.driver, Duration.ofSeconds(sec * 1000)); //Creating wait 
		Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {
			public Boolean apply(WebDriver arg0) {

				boolean isLoaded = false;
				JavascriptExecutor js = (JavascriptExecutor) arg0;
				if (js.executeScript("return document.readyState").toString().equalsIgnoreCase("complete")) { //Js wait for dom ready.
					isLoaded = true; //Checking if page is ready to interact or not
					logger.info("Web Page loaded successfully.");
				}
				return isLoaded;

			}
		};
		wait.until(function);

	}

	// Hard wait for specific second :
	public static void waitFor(int sec) { 

		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace(); //Print detail error log with strace.
		}

	}

	public static void waitForMlsec(long sec) { //Wait for milisec

		try {
			Thread.sleep(sec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<WebElement> waitForTableRows(By locator) { //Waits for ALL table rows to be ready.

		logger.info("Waiting for multiple <tr> elements to be visible and enabled on the screen");
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(ELEMENT_WAIT_TIMEOUT_SECONDS)); //Creates wait
		ArrayList<WebElement> rowElements = new ArrayList<>(); //Create a element list
		try {
			// Wait until at least one <tr> element is present
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator)); //waiting for element to be present.
			// Wait until all <tr> elements are visible and enabled
			List<WebElement> elements = wait.until(driver -> {
				List<WebElement> foundElements = driver.findElements(locator);
				boolean allVisibleAndEnabled = foundElements.stream().allMatch(e -> e.isDisplayed() && e.isEnabled()); //Checks if all displayed and enabled element
				return allVisibleAndEnabled ? foundElements : null; //If all enabled and displayed only then return element list otherwise return null.
			});
			// Convert to ArrayList
			rowElements = new ArrayList<>(elements);
			// Optional: Visual feedback for each row
			for (WebElement row : rowElements) {
				drawborder(row);
				// flash(row);
			}
			logger.info("All table row elements are now visible and enabled.");
		} catch (TimeoutException e) {
			logger.warn(LogColor.RED + "Table row elements with locator " + locator + " not found within timeout."
					+ LogColor.RESET);
		}
		return rowElements;

	}

	public void waitForNetworkIdle() { //Waits for all network requests to complete

		logger.info("Wait for Web Page to load completely");
		JavascriptExecutor js = (JavascriptExecutor) driver; //JavaScript calling to execute
		long lastCount = -1; // -1 means no previous value is initialized.
		long sameCountTimes = 0;
		for (int i = 0; i < 100; i++) {
			long currentCount = (long) js.executeScript( //Counting http requests.
					"return window.performance.getEntriesByType('resource').filter(r => !r.responseEnd).length;");  //Checking loading/incomplete resource requests.
			if (currentCount == lastCount) {
				sameCountTimes++;
				if (sameCountTimes >= 3)
					break;
			} else {
				sameCountTimes = 0;
			}
			lastCount = currentCount;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

	}

	public WebElement waitForElement(WebElement locator) { //Wait for  WebElemen visible

		logger.info("Checking visibility of the Element on browser screen");
		// Prioritize ExpectedConditions for efficiency
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(ELEMENT_WAIT_TIMEOUT_SECONDS));
		try {
			wait.until(ExpectedConditions.visibilityOf(locator));
		} catch (TimeoutException e) {
			// Handle timeout exception (optional, log or throw as needed)
			logger.warn("Element with locator " + locator + " not found within timeout.");
			logger.error(LogColor.RED + "Exception occurred: ", e + LogColor.RESET);
		}
		// Check element visibility and enabled state only if necessary
		if (!locator.isDisplayed() || !locator.isEnabled()) {
			// Consider using a more specific ExpectedCondition or custom implementation
			wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {

					if (locator.isDisplayed() && locator.isEnabled()) {
						return locator; //If displayed and enabled then return and interact with element.
					} else {
						return null; // Wait for element to become both visible and enabled
					}

				}
			});
		}
		// Draw border and flash (consider optimization techniques)
		drawborder(locator); // Assuming `drawBorder` takes WebElement as parameter
//		flash(locator); // Assuming `flash` takes WebElement as parameter
		logger.info("Wait for the element is completed, Element is visible on the screen");
		return locator;

	}

// browserUtils	
	@SuppressWarnings("deprecation")  // Its use to ignore outdated warning by java compiler
	public static WebElement fluentWait(final WebElement webElement, int timeinsec) { //Flexible wait with polling. For dynamic elements

		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(Driver.getDriver())
				// .withTimeout(timeinsec, TimeUnit.SECONDS).pollingEvery(timeinsec,
				// TimeUnit.SECONDS)
				.withTimeout(Duration.ofSeconds(timeinsec)).pollingEvery(Duration.ofSeconds(timeinsec)) // Repeatedly checking
				.ignoring(NoSuchElementException.class);
		WebElement element = wait.until(new Function<WebDriver, WebElement>() { // Wait for repeatedly check for valid web element
			public WebElement apply(WebDriver driver) {

				return webElement; // Simply returns web element

			}
		});
		return element;

	}

	// Wait for the Title of a webpage
	public boolean waitForTitle(String pageTitle) {

		logger.info("Wait for Page title to load");
		wait = new WebDriverWait(driver, Duration.ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS));
		return wait.until(ExpectedConditions.titleIs(pageTitle));

	}

	public void waitForClickablility(WebElement locator) { //wait for element to clickable.

		WebElement element;
//		try {
		WebDriverWait wait = new WebDriverWait(Driver.getDriver(), Duration.ofSeconds(ELEMENT_WAIT_TIMEOUT_SECONDS));
		element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		/*
		 * System.out.println("waiting till page is loaded successfully ");
		 * wait.until(ExpectedConditions.and(
		 * ExpectedConditions.elementToBeClickable(locator),
		 * ExpectedConditions.jsReturnsValue("return document.readyState == 'complete';"
		 * ) ));
		 * 
		 * System.out.println("page is loaded successfully ");
		 */
		if (element.isDisplayed() && element.isEnabled()) {
			drawborder(element);
			logger.info("element is visible and Clickable");
		} else {
			logger.info("Element is not clickable");
		}

//		}
		/*
		 * catch (Exception e) {
		 * 
		 * 
		 * WebDriverWait wait = new WebDriverWait(Driver.getDriver(),
		 * Duration.ofSeconds(timeout)); element=
		 * wait.until(ExpectedConditions.elementToBeClickable(locator));
		 * 
		 * logger.info("is element clickable ? : " +element.isEnabled()); }
		 */
	}
	// Wait for an Alert to appear

	public Alert waitForAlert() { //wait for pop-up alert

		logger.info("Wait for An alert to appear");
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver) //Dynamic wait for JavaScript alert
				.withTimeout(Duration.ofSeconds(ELEMENT_WAIT_TIMEOUT_SECONDS))
				.pollingEvery(Duration.ofMillis(ELEMENT_POLLING_TIME_MILIS)).ignoring(NoAlertPresentException.class);
		Function<WebDriver, Alert> function = new Function<WebDriver, Alert>() { // wait for JavaScript alert/pop-up dialogs to appear
			public Alert apply(WebDriver arg0) {

				logger.info("Waiting for the alert");
				Alert alert = driver.switchTo().alert(); //Handling pop-up alert.
				return alert;

			}
		};
		return wait.until(function);

	}
	
	public String getAlertText() { //Get's text from alert.
	    try {
	        // Switch to the alert
	        Alert alert = waitForAlert();

	        // Get the text
	        String alertText = alert.getText();

	        // Log it for debugging
	        logger.info("⚠️ Alert text captured: " + alertText);

	        return alertText;
	    } catch (NoAlertPresentException e) {
	        throw new IllegalStateException("No alert is present to capture text.", e); //Display message if no exception found.
	    }
	}

	// wait for URL
	public static void waitForUrlContains(String expectedSubstring) { //Wait to get url specific content text.

		try {
			logger.info("waiting for URL to Contain: " + expectedSubstring);
			Duration timeToWaitInSec = Duration.ofSeconds(PAGE_LOAD_TIMEOUT_SECONDS);
			WebDriverWait wait = new WebDriverWait(Driver.getDriver(), timeToWaitInSec);
			Boolean URlContainsExpect = wait.until(ExpectedConditions.urlContains(expectedSubstring)); //Wait till get a substring or text from url.
			logger.info("URL Contains : " + expectedSubstring);
		} catch (Exception e) {
			logger.info("URL Doesn't Contains : " + expectedSubstring);
		}

	}

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------
	 * --------------------------------------------------------Different Element
	 * Display Methods--------------------------------------------------------
	 * -------------------------------------------------------------------------------------------------------------------------------------------
	 **/
	public boolean isElementPresent(By locator) { //checking if element is exist or not

		// Save the current implicit wait
		Duration originalImplicitWait = driver.manage().timeouts().getImplicitWaitTimeout();
		// Set implicit wait to zero
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(200)); // 1 second timeout
		try {
			removeBorder(); // removes border from previous element
//		        wait.until(ExpectedConditions.presenceOfElementLocated(locator)); 
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
			drawborder(driver.findElement(locator));
			removeBorder();
			logger.info("isElelementPresent=True");
			return true;
		} catch (TimeoutException e) {
			return false;
		} finally {
			// Restore the original implicit wait
			driver.manage().timeouts().implicitlyWait(originalImplicitWait);
		}

	}

	public boolean isElementDisplayed(WebElement locator) { //checking if element is displayed or not.

		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
		// .implicitlyWait(0, TimeUnit.SECONDS);
		logger.info("Checking visibility of the Element on browser screen");
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(30)) //Wait for repeatedly Checking polling/dynamic element
					.pollingEvery(Duration.ofMillis(ELEMENT_POLLING_TIME_MILIS)).ignoring(Exception.class);
			Function<WebDriver, Boolean> function = new Function<WebDriver, Boolean>() {
				public Boolean apply(WebDriver arg0) {

					Boolean isPresent = false;
					if (locator.isDisplayed()) {
						isPresent = true;
						logger.info("Wait for the element is completed, Element is visible on the screen");
					}
					return isPresent;

				}
			};
			boolean e = wait.until(function);
			return e;
		} catch (Exception e) {
			return false;
		}

	}

	public boolean isElementStableAndVisible(WebElement element) { //Checking Element Stability and visibility to interact with.

		// Save the current implicit wait
		Duration originalImplicitWait = driver.manage().timeouts().getImplicitWaitTimeout();
		// Set implicit wait to zero
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(300)); // Slightly longer timeout
		try {
			removeBorder(); // Optional: clear previous highlights
			// Wait until the element is visible and not stale
			wait.until(driver -> {
				try {
					return element.isDisplayed();
				} catch (TimeoutException e) {
					return false;
				}
			});
			drawborder(element); // Optional: highlight the element
			removeBorder();  //Removing previous borders.
			logger.info("isElementStableAndVisible=True");
			return true;
		} catch (TimeoutException e) {
			logger.warn("Element not stable or visible within timeout.");
			return false;
		} finally {
			// Restore the original implicit wait
			driver.manage().timeouts().implicitlyWait(originalImplicitWait);
		}

	}

	public void highlightElement(WebElement locator) { //Highlighting element interactions for better visibility to debug in future.

		removeBorder(); //Removes previous highlights.
		for (int i = 0; i < 3; i++) {
			drawborder(locator); //loop for creating repeatedly 3 times blinking and removing highlights
			removeBorder();
		}

	}

	public boolean isElementPresentbyJS_ShadowRoot(By locator) { //Checks if an element exists within Shadow DOM using JavaScript

//    	waitFor(3);
		try {
			String script = "return arguments[0].shadowRoot || arguments[0].getRootNode().host || arguments[0].getRootNode()";
			WebElement element = driver.findElement(locator);
			Object shadowRoot = ((JavascriptExecutor) driver).executeScript(script, element); //checking if element is inside shadow dom or not by running JavaScript Command.
			return shadowRoot != null;
		} catch (NoSuchElementException e) {
			return false;
		}

	}

	public boolean isElementPresentbyJS(By locator) {  //Checks if an element exists in the DOM using JavaScript command.

		try {
			waitForElement(locator);
			WebElement element = driver.findElement(locator);
			return true; // Element found
		} catch (NoSuchElementException | TimeoutException e) {
			return false; // Element not found
		}

	}

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------
	 * -------------------------------------------------------- Java Script
	 * Utilities ------------------------------------------------------------
	 * -------------------------------------------------------------------------------------------------------------------------------------------
	 **/
	public static void jsclick(WebDriver driver, WebElement element) {  //Performs a click on a web element using JavaScript

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].click()", element); //Executes JavaScript click on the specified web element.

	}
	
	

	// import org.apache.logging.log4j.Logger; (If not already present)

	/**
	 * Converts a date string from any common format (M/d/yyyy) to the standard 
	 * HTML input format (yyyy-MM-dd). This ensures date validation passes.
	 *
	 * @param dateValue The date string from the Excel file (e.g., "10/16/1980").
	 * @param sourceFormat The format of the dateValue (e.g., "M/d/yyyy" for flexible Excel dates).
	 * @return The date string in the target format (e.g., "1980-10-16").
	 */
	public static String standardizeDateFormat(String dateValue, String sourceFormat) { //Converts a date from one format to a standardized format.
	    
	    // The target format is the standard HTML input type="date" value format
	    final DateTimeFormatter TARGET_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    
	    // Create the formatter based on the format of the data coming from Excel
	    final DateTimeFormatter SOURCE_FORMATTER = DateTimeFormatter.ofPattern(sourceFormat);
	    
	    try {
	        // 1. Parse the date string from the source format
	        LocalDate date = LocalDate.parse(dateValue.trim(), SOURCE_FORMATTER);
	        
	        // 2. Format the LocalDate object into the target format
	        String targetDate = date.format(TARGET_FORMATTER);
	        
	        // Log success (optional, but helpful)
	        logger.debug("Date standardized: " + dateValue + " -> " + targetDate);
	        
	        return targetDate; //Return date in expected format.
	        
	    } catch (Exception e) {
	        // If parsing fails (e.g., date value is garbage, blank, or not in expected format)
	        logger.error("❌ Failed to standardize date format. Value: '" + dateValue + 
	                     "' Source Format: '" + sourceFormat + "'. Error: " + e.getMessage());
	        
	        // Re-throw as a clean RuntimeException to stop the test with clear context
	        throw new RuntimeException("Date standardization failed for value: " + dateValue, e);
	    }
	}

	// Assume driver is accessible in CommonMethods (e.g., public static WebDriver driver;)

	// Inside CommonMethods.java

	/**
	 * The most robust method for date fields, temporarily bypassing stubborn app validation 
	 * and forcing the standardized date value (YYYY-MM-DD) into the element.
	 */
	// Inside CommonMethods.java (or your primary utility class)
	// Imports for logging, date/time, etc., should be present

	/**
	 * 🌟 ONE-STOP SHOP for setting date values. 
	 * Standardizes the date format (e.g., 10/16/1980 -> 1980-10-16), 
	 * sets the value using JavaScript to bypass validation, verifies the input, 
	 * and handles all logging.
	 *
	 * @param element The target Date input WebElement.
	 * @param excelDateValue The raw date string from Excel (e.g., "10/16/1980").
	 * @return true if the field's value attribute matches the expected standardized date, false otherwise.
	 */
	public static boolean safeSetDateValue(WebElement element, String excelDateValue) {  //Safely sets a date value to a web element after converting it from Excel format to the required input format.
	    
	    // 1. STANDARDIZATION: Convert the expected date to the browser's required format (YYYY-MM-DD)
	    // Assume M/d/yyyy is the most common format from Excel.
	    String expectedStandardizedDate;
	    try {
	        expectedStandardizedDate = standardizeDateFormat(excelDateValue, "M/d/yyyy"); //Convert Excel date in "M/d/yyyy" format to a standardized date format.
	    } catch (Exception e) {
	        logger.error("❌ Failed to standardize date format for value: " + excelDateValue + ". Aborting input.", e);
	        return false;
	    }

	    // 2. JS EXECUTION SETUP (Includes Validation Bypass)
	    JavascriptExecutor js = (JavascriptExecutor) driver; // Assumes 'driver' is static/accessible

	    String originalOnKeyUp = element.getAttribute("onkeyup"); // JavaScript function assigned to the onkeyup(Repeated) event handler of a web element.
	    String originalOnBlur = element.getAttribute("onblur");  // JavaScript function assigned to the onblur(Final) event handler of a web element.
	    
	    String script = 
	        // Temporarily disable the application's validation handlers
	        "arguments[0].onkeyup = null;" +
	        "arguments[0].onblur = null;" +
	        
	        // Set the value using the CORRECT, STANDARDIZED format
	        "arguments[0].value = arguments[1];" +
	        
	        // Fire the change event to ensure internal framework registration
	        "arguments[0].dispatchEvent(new Event('change'));";
	        
	    // 3. EXECUTE, LOG, and RESTORE HANDLERS
	    try {
	        js.executeScript(script, element, expectedStandardizedDate);
	        
	        // Restore the original handlers
	        js.executeScript("arguments[0].onkeyup = arguments[1];", element, originalOnKeyUp);
	        js.executeScript("arguments[0].onblur = arguments[1];", element, originalOnBlur);

	        // 4. SELF-VERIFICATION
	        String actualValue = element.getAttribute("value").trim();
	        
	        if (expectedStandardizedDate.equals(actualValue)) { //if successfully entered date then log
	            logger.info("✅ Date Input Success: Field populated with verified value: " + actualValue);  //if successful entered date then log with value.
	            return true;
	        } else {
	            logger.error(String.format("❌ Date Input FAILURE: Value mismatch after set. Expected: [%s] | Actual: [%s]. The application's JS validation may be too aggressive.",
	                                    expectedStandardizedDate, actualValue.isEmpty() ? "<BLANK>" : actualValue)); //Log failure if date successfully not entered.
	            return false;
	        }

	    } catch (Exception e) {
	        logger.error("❌ JS Execution Failed to set date value for element: " + element.toString(), e);
	        // Do not throw a RuntimeException here; return false to allow softAssert to handle it.
	        return false; 
	    }
	}
	

	public static void scrollIntoView(WebDriver driver, WebElement element) {  //Scrolls the webpage to make a specific element visible on screen.

		JavascriptExecutor js = (JavascriptExecutor) driver; // Calling JavaScript
		js.executeScript("arguments[0].scrollIntoView(true);", element); //Scroll to that specific element

	}

	public static void scrollbottom(WebDriver driver) {  //Scrolls to the very bottom of the webpage.

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0,document.body.scrollHeight)"); 

	}

//     
	public static void changecolour(String color, WebElement element, WebDriver driver) { //Changes an element's background color for visual highlighting during test execution.

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].style.backgroundColor='" + color + "'", element); //Changing background color temporarily.
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//     https://html-color.codes/
	public static void flash(WebElement element) {  //Makes an element blink/flash

		String bgcolor = element.getCssValue("backgroundColor");
		System.out.println(bgcolor);
		for (int i = 0; i < 3; i++) {  //blink/flash 3 times
//        		changecolour("#0000FF", element, driver); //blue
			changecolour("#f08080", element, driver);
			changecolour(bgcolor, element, driver);
		}
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void drawborder(WebElement element) { //Draw border for highlighting elements.

		removeBorder();
//        	JavascriptExecutor js = (JavascriptExecutor)driver;	
		js.executeScript("arguments[0].style.border='3px solid red'", element);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void removeBorder() {

		// Remove the border by setting the element's style
		JavascriptExecutor js = (JavascriptExecutor) driver;

		// Check for existing borders and remove them if present
		String scriptCheckAndRemoveBorders = "var elements = document.querySelectorAll('*');"
				+ "var borderExists = false;" + "for (var i = 0; i < elements.length; i++) {"
				+ "    if (window.getComputedStyle(elements[i]).border !== 'none') {" + "        borderExists = true;"
				+ "        break;" + "    }" + "}" + "if (borderExists) {"
				+ "    for (var i = 0; i < elements.length; i++) {" + "        elements[i].style.border='';" + "    }"
				+ "}";
		js.executeScript(scriptCheckAndRemoveBorders);
		// Wait for 3 seconds (adjust the duration as needed)
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void generateAlert(WebDriver driver, String message) {  //Creates a JavaScript alert pop-up

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("alert('" + message + "')");  //alert popup with a custom message

	}
	
    // Accepts the alert and returns its text
    public static void acceptAlert() {
        try {
            Alert alert = driver.switchTo().alert();

            // Capture the text before accepting
            String alertText = alert.getText();
           logger.info("⚠️ Alert text: " + alertText);

            // Click OK
            alert.accept();

           
        } 		
        
        catch (Exception e) {
			logger.error(LogColor.RED + e + LogColor.RESET);
			
		}
    }


	public void drawAndFlash(WebElement element) { //Draw border first then blink/flash the highlights

		drawborder(element);
		flash(element);

	}

	public void clickAndDraw(WebElement element) { // Draw a border to highlight when clicking on element.

		removeBorder();
		waitForClickablility(element);
		// hoverAndClick(element);
		hoverOver(element); //hovering the mouse over a web element without clicking it.
		jsclick(driver, element); //JS click on element
		waitForPageAndAjaxToLoad();

	}

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------
	 * --------------------------------------------------------Different Reusable
	 * Methods--------------------------------------------------------
	 * -------------------------------------------------------------------------------------------------------------------------------------------
	 **/
	// Verify Title :
	public boolean verifyPageTitle(String expectedTitle) { //Checks if the current page title matches the expected title after waiting for the page to fully load.
		
		waitForPageAndAjaxToLoad();
	    if (expectedTitle == null || expectedTitle.isEmpty()) {
	        logger.error(LogColor.RED + "Expected title is null or empty!" + LogColor.RESET);
	        return false;
	    }

	    // Get actual title from browser
	    String actualTitle = driver.getTitle();

	    // Print both titles
	    logger.info(LogColor.DarkSlateBlue + "Expected Title: " + expectedTitle + LogColor.RESET);
	    logger.info(LogColor.DarkSlateBlue + "Actual Title From UI: " + actualTitle + LogColor.RESET);

	    // Compare
	    if (actualTitle.equals(expectedTitle.trim())) {
	        logger.info(LogColor.DarkGreen + "✅ Page title matched!" + LogColor.RESET);
	        return true;
	    } else {
	        logger.error(LogColor.RED + "❌ Page title mismatch!" + LogColor.RESET);
	        return false;
	    }
	}
	// Switch to Other Window:

	public void switchToAnotherWindow(String CurrentWin) { //Handle new window.

		wait.until(ExpectedConditions.numberOfWindowsToBe(2));
		Set<String> windows = driver.getWindowHandles(); //Gets all the unique windows in a set.
		for (String s : windows) {
			if (!s.equalsIgnoreCase(CurrentWin)) {
				driver.switchTo().window(s);
				break;
			}
		}

	}

	public void hoverOver(WebElement element) { //hovering the mouse over a web element without clicking it.

		try {
			drawborder(element);
			Actions actions = new Actions(driver);
			actions.moveToElement(element).build().perform(); //mouse hover perform
		} catch (NoSuchElementException e) {
			logger.info("Draw Border failed as element not present= " + element);
		}

	}
	
	

	public boolean checkDownloadAndDelete(String expectedFileName) { //Download then delete to clear records

		String projectPath = System.getProperty("user.dir") + "\\Downloads"; //Getting the folder path to store download file.
		boolean isFilePresent = new WebDriverWait(driver, Duration.ofSeconds(60)).until(driver -> {
			File dir = new File(projectPath);
			File[] files = dir.listFiles();
			if (files != null) { //Checking if file exists
				for (File file : files) {
					if (file.getName().contains(expectedFileName)) {
						return true;
					}
				}
			}
			return false;
		});
		// Clean up the file after validation
		if (isFilePresent) {
			File dir = new File(projectPath);
			for (File file : dir.listFiles()) {
				if (file.getName().contains(expectedFileName)) {
					file.delete(); //Performing delete operation.
				}
			}
		}
		return isFilePresent;

	}

	public boolean checkDownloadAndDelete(String expectedFileName1, String expectedFileName2,  //Download then delete to clear records for specific 3 type multiple files.
			String expectedFileName3) {

		String projectPath = System.getProperty("user.dir") + "\\Downloads";  //Getting the folder path to store download file.
		boolean isFilePresent = new WebDriverWait(driver, Duration.ofSeconds(60)).until(driver -> {
			File dir = new File(projectPath);
			File[] files = dir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().contains(expectedFileName1) && file.getName().contains(expectedFileName2) //Checking all 3 files
							&& file.getName().contains(expectedFileName3)) {
						return true;
					}
				}
			}
			return false;
		});
		// Clean up the file after validation
		if (isFilePresent) {
			File dir = new File(projectPath);
			for (File file : dir.listFiles()) {
				if (file.getName().contains(expectedFileName1) && file.getName().contains(expectedFileName2)
						&& file.getName().contains(expectedFileName3)) {
					file.delete();
				}
			}
		}
		return isFilePresent;

	}

	public void hoverOver(By elem) { //Hover over the mouse pointer

		try {
			WebElement element = driver.findElement(elem);
			drawborder(element);
			Actions actions = new Actions(driver);
			actions.moveToElement(element).build().perform();
		} catch (NoSuchElementException e) {
			logger.info("Draw Border failed as element not present= " + elem);
		}

	}

	public void hoverAndClick(WebElement element) { //Hover over then click

		drawborder(element);
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform(); //Performing click after hover over mouse pointer.
		waitForMlsec(500);

	}
//------------------------------------------ Screenshot Try -----------------------------------------//

	public void captureAndAttachScreenshot(Scenario scenario, String screenshotName) {  //Takes screenshots and attaches it to Cucumber test report with error handling.

		try {
			logger.info("Capturing Screenshot");
			// Take screenshot
			final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			// Attach screenshot to the report
			scenario.attach(screenshot, "image/png", screenshotName);
			logger.info("Screenshot Attached");
		} catch (Exception e) {
			logger.error("Error capturing screenshot: " + e.getMessage());
			// Optionally attach a message to the scenario
			scenario.attach(("Error capturing screenshot: " + e.getMessage()).getBytes(), "text/plain", "error");
		}

	}
	// Get text of an Element

	public String getElementText(WebElement element) {

		return element.getAttribute("textContent").trim(); //Get the actual text content without extra spaces.

	}

	// Get attribute value of an Element
	public String getAttributeValue(WebElement element, String attributeName) {

		if ("text".equalsIgnoreCase(attributeName)) { //Checking case insensitiveness.
			return getElementText(element);
		} else {
			return element.getAttribute(attributeName);
		}

	}

	public void scrollToElement(WebElement element) {

		((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + element.getLocation().y + ")");  //Scrolls the page vertically

	}
	// Compare two lists

	public boolean listCompare(List<String> expectedList, List<String> inputList) { //Check expected and input element list of strings

		boolean status = false;
		try {
			if (inputList != null && expectedList != null && (inputList.size() == expectedList.size())) {
				for (String opt : inputList) {
					if (expectedList.contains(opt.trim())) {
						status = true;
					} else {
						status = false;
					}
				}
			}
		} catch (Exception e) {
			status = false;
		}
		return status;

	}
	// Check file is downloaded in the given path

	public boolean isFileDownloaded(String filePath, String fileName) throws InterruptedException {  //Check file after Download if its downloaded or not.

		boolean isFilePresent = false;
		try {
			File dir = new File(filePath);
			Thread.sleep(15000);
			File[] dir_contents = dir.listFiles(); //Getting file list
			for (int i = 0; i < dir_contents.length; i++) {
				if (dir_contents[i].getName().contains(fileName)) //Checks to match file name.
					isFilePresent = true;
			}
		} catch (Exception e) {
			isFilePresent = false;
		}
		return isFilePresent;

	}

	public boolean isFileUpload(String filePath, String fileName) throws InterruptedException { //Checks if file is uploaded or not.

		boolean isFilePresent = false;
		try {
			File dir = new File(filePath); //Desired file path to lookup.
			Thread.sleep(8000);
			File[] dir_contents = dir.listFiles(); //Getting file lists
			for (int i = 0; i < dir_contents.length; i++) {
				if (dir_contents[i].getName().contains(fileName)) //Checks the file by file name
					isFilePresent = true;
			}
		} catch (Exception e) {
			isFilePresent = false;
		}
		return isFilePresent;

	}

	public static void switchToWindowbyTitile(String targetTitle) {  //Switch to a browser window that has a specific title.

		String origin = Driver.getDriver().getWindowHandle(); //Getting window handle.
		for (String handle : Driver.getDriver().getWindowHandles()) {
			Driver.getDriver().switchTo().window(handle); //switch to new window
			if (Driver.getDriver().getTitle().equals(targetTitle)) { //checking if is on expected tilled window page.
				return;
			}
		}
		Driver.getDriver().switchTo().window(origin); //Switch back to previous page.

	}

	public String getChildWindowTitle() {  //Getting the first child window.

		String title = "";
		String mainWindow = driver.getWindowHandle(); //Getting window handler
		Set<String> set = driver.getWindowHandles(); //Creating a set for storing windows to take first 1 from list.
		Iterator<String> itr = set.iterator(); //Iterator to iterate the set value.
		if (itr.hasNext()) {
			while (itr.hasNext()) {
				String childWindow = itr.next(); 
				if (!mainWindow.equals(childWindow)) { // Comparing with main window
					driver.switchTo().window(childWindow);
					waitForPageAndAjaxToLoad();
					title = driver.switchTo().window(childWindow).getTitle(); //Getting title
					driver.close();
				}
			}
			driver.switchTo().window(mainWindow); // Get back to main window
		} else {
			logger.info("No Child window Opened");
			title = driver.getTitle();
		}
		logger.info("title of window " + title);
		return title;

	}

	public int getOpenWindowsCount() { //Count open windows list.

		try {
			Thread.sleep(4000);
		} catch (Exception e) {
		}
		// Get all open windows
		Set<String> windowHandles = driver.getWindowHandles();
		// Return the count of open windows
		return windowHandles.size();

	}

	public void selectFromDropdownByIndex(WebElement el, List<WebElement> optionList, int index) throws Exception {  //Select options from dropdown by its index position

		try {
			el.click();
			// List<WebElement>
			// optionList=driver.findElements(By.xpath("//div[contains(@class,'ui-selectmenu-open')]//a"));
			Thread.sleep(5000);
			for (int i = 0; i < optionList.size(); i++) { //Loop for searching options by index
				if (i == index) {
					waitForElement(optionList.get(i)).click(); //Wait and math index number
					break;
				}
			}
		} catch (Exception e) {
			throw new Exception("Failed to click on option at index " + index);
		}

	}

	public static Select selectFromDropDownbyVisibleText(WebElement dropdown, String optionName) { //Select options from dropdown by visible text

		dropdown.click(); //Click on dropdown
		waitFor(1); //Wait for 1 sec
		Select select = new Select(dropdown);
		// List<WebElement> lis = Driver.getDriver().findElements(By.xpath(dropdown));
		// CommonMethods.waitFor(2);
		select.selectByVisibleText(optionName);
		// dropdown.click();
		return select;

	}

	public static Select selectFromDropDownbyValue(WebElement dropdown, String optionName) {  //Select options from dropdown by value

		dropdown.click();
		waitFor(1);
		Select select = new Select(dropdown);
		// List<WebElement> lis = Driver.getDriver().findElements(By.xpath(dropdown));
		// CommonMethods.waitFor(2);
		// select.selectByVisibleText(optionName);
		select.deselectByValue(optionName.trim()); //Removes white spaces by trim
		// dropdown.click();
		return select;

	}

//	   From Browser Utils
	public static String selectFromropDownRendomOption(WebElement dropdown) { //Select random options from dropdown text

		Select select = new Select(dropdown);
		List<WebElement> i = select.getOptions();
		int size = i.size();
		int rendomeOption = randInt(0, (size - 1));  // Generates random index within the dropdown's option range
		String eachOption = i.get(rendomeOption).getText().trim(); //Get random options by removing extra spaces
		return eachOption;

	}

	public static int dropDownElementsInTotal(WebElement dropdown) {  //The total count of all available options in a dropdown.

		dropdown.click();
		Select dropDown = new Select(dropdown);
		List<WebElement> e = dropDown.getOptions();
		int itemCount = e.size();  //Counts the total number of dropdown options
		return itemCount;

	}

//	   From Browser Utils
	public static Select selectFromdropDownByStateAbrivation(WebElement dropdown, String optionName) {  //Selects a dropdown option containing the specified text/Code

		dropdown.click();
		Select dropDown = new Select(dropdown);
		List<WebElement> e = dropDown.getOptions();
		int itemCount = e.size();
		for (int l = 0; l < itemCount; l++) {
			logger.info(e.get(l).getText());
			if (e.get(l).getText().trim().contains(optionName)) {  //Compares each dropdown option's text with the provided string using contains() for partial matching
				dropDown.selectByIndex(l);
				break;
			}
			continue;
		}
		return dropDown;

	}

//	   From Browser Utils
	public static void checkBoxYesNO(WebElement checkBox, String fromExcel) { //Checking check box yes/no from excel data.

		if (fromExcel.equalsIgnoreCase("Yes") && checkBox.isSelected()) {
			logger.info("Check Box already selected for " + checkBox);
		} else if (fromExcel.equalsIgnoreCase("No") && !checkBox.isSelected()) {
			logger.info("Check box is not selected and not will be selected becouse is No for " + checkBox);
		} else {
			checkBox.click();
		}

	}

	public static void checkBox(WebElement webElement, String option) {  //Handles check box interaction

		switch (option) {
		case "yes":
			webElement.click();
			break;
		case "Yes":      //Case insensitiveness handle by using case.
			webElement.click();
			break;
		case "no":
			logger.info("There is no in excel sheet no Suppress Interest Calculation");
		}

	}

	/**
	 * 
	 * Scroll screen to a particular element
	 * 
	 * 
	 * 
	 * @param element
	 * 
	 */
	public void scrollScreen(WebElement element) {  //Scroll to view particular element

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: \"center\", inline: \"center\"});", element);  //Horizontally and vertically in center element

	}

// **********************  Random Number Methods *******************************
	public static int randInt(int min, int max) {  //Generates random integer by min max range

		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min; //ensures max value is inclusive.
		return randomNum;

	}

	public String getRandomNumber() {  //Generates a random positive integer within a limited range by dividing a large random number.

		Random r = new Random();
		int num = Math.abs(r.nextInt()) / 10000;  //The division by 10,000 reduces the possible range from billions to thousands ; small number to manage well
		;
		return Integer.toString(num);

	}

	public static int getDecimalRandomNumber() { //Generates random integer with decimal value

		// create instance of Random class
		Random rand = new Random();
		// Generate and return Random number with decimal
		return rand.nextInt();

	}

	public static void rendomFromDD(String webelement) {  //Randomly clicks an element from a list/dropdown located by an XPath string.

		List<WebElement> options = Driver.getDriver().findElements(By.xpath(webelement)); //Finding element by xpath
		Random rand = new Random();
		int list = rand.nextInt(options.size()); //Generates a random index
		options.get(list).click(); //Clicking

	}

	public static int rendomNumberWithin(int min, int max) {  //Generates a random integer within a specified inclusive range [min, max]

		// define the range
		int range = max - min + 1;
		int rand = 0;
		// generate random numbers within 1 to 10 i=min-1,i<max,i++
		for (int i = min; i <= max; i++) {
			rand = (int) (Math.random() * range) + min;
			// Output is different everytime this code is executed
			// logger.info("FROM INSIDE FOREACH "+rand);
		}
		return rand;

	}
	
//********************** End Of - Random Number Methods *******************************

//	   From Browser Utils: 
	public static ArrayList<String> removeDuplicates(ArrayList<String> manufacturerCodeList2) {  //Remove duplicate Strings from list while inserting.

		Set<String> set = new LinkedHashSet<>();  //Set is for unique value
		set.addAll(manufacturerCodeList2);
		manufacturerCodeList2.clear();  //Clears all previous manufacturer code list data
		manufacturerCodeList2.addAll(set); //Re adding using set to get unique value.
		// logger.info("manufacturerCodeList2");
		return manufacturerCodeList2;

	}

	public static int numberOfTheRowsDynamicTable(String partOfTheXpath) { //Dynamically counts table row

		List<WebElement> rows = Driver.getDriver().findElements(By.xpath(partOfTheXpath)); //Parameterized locator to find the rows, (reusable).
		int rowNumber = rows.size(); //Counts the row number
		return rowNumber;

	}

	public String GetFuture_EST_Date(int days) {  //Generates future EST date

		SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy"); //Defines date format
		Date date = new Date();
		sd.setTimeZone(TimeZone.getTimeZone("EST")); //Setting time zone to EST=Eastern Standard Time
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return sd.format(cal.getTime()); //Return modified formated date.

	}
//Shams Addition	 

	public String yesterdaysDate() { // Get the Yesterday date

		Date currentDate = new Date();
		// Subtract 2 days from the current date
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		calendar.add(Calendar.DATE, -2);
		Date modifiedDate = calendar.getTime();
		// Define the desired date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		// Convert the modified date to the desired format
		String formattedDate = dateFormat.format(modifiedDate);
		// Print the formatted date
		logger.info("Current date minus 2 days in mm/dd/yyyy format: " + formattedDate);
		return formattedDate;

	}

	public void clearDownloadFolder() throws IOException { //Clears Download folder for clean new records

		String projectPath = System.getProperty("user.dir");
		String downloadFoderPath = "runtime" + File.separator + "downloads"; //Get the download path
		File downloadFolder = new File(projectPath + File.separator + downloadFoderPath);
		if (downloadFolder.exists()) {
			if (downloadFolder.listFiles().length > 0) {
				for (File file : downloadFolder.listFiles()) {
					FileDeleteStrategy.FORCE.delete(file); //Force delete file
				}
			}
		} else {
			downloadFolder.mkdir(); //Creates new download folder if not found download folder.
		}

	}

	public int getFileCountInDownloadFolder() {  //Counts the files in download folder

		String projectPath = System.getProperty("user.dir");
		String downloadFoderPath = "runtime" + File.separator + "downloads";
		File downloadFolder = new File(projectPath + File.separator + downloadFoderPath);
		File[] dir_contents = downloadFolder.listFiles(); //Get files list
		return dir_contents.length;

	}

	/**
	 * 
	 * Get weekend dates of a given month & year
	 * 
	 * 
	 * 
	 * @param month - month of a Year
	 * 
	 * @param year  - Year
	 * 
	 * @return
	 * 
	 */
	public String leftPadStringWithLeadingZeroes(Integer n, String str) { //Generate left zeros for desired format or digits number

		// n -> Size of the string to be generated
		// str -> String to be padded with Zeros
		String format = "%0" + n + "d"; //Padding rule formatted with 0
		String str1 = String.valueOf(String.format(format, Integer.parseInt(str))); //Getting string value and format it
		return str1; // return String

	}

	public String getCurrentDate(String dateFormate) { //Getting the current date 

		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormate);
		Date d = new Date();
		return dateFormat.format(d).toString();

	}

	public String getDateInFormat(String input, String inputDF, String outputDF) { //Convert Date from one format to another

		String finalInput = input;
		if (inputDF.length() - input.length() > 0) {
			finalInput = leftPadStringWithLeadingZeroes(inputDF.length(), input); //Adding left padding with zeroes if needed
		}
		DateFormat fmt1 = new SimpleDateFormat(inputDF); 
		Date date = null;
		try {
			date = fmt1.parse(finalInput); //Converts date string to java object
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DateFormat fmt2 = new SimpleDateFormat(outputDF);
		String reqFmtDate = fmt2.format(date);
		return reqFmtDate;

	}

     //	 From Browser Utils
	public static String date(String dateFromExcel) { //Converting excel date into formatted date

		if (dateFromExcel.substring(0, 1).equals("0")) {  //Checks if first character of date is zero or not.
			String date = months(dateFromExcel.substring(3, 6)) + "/" + dateFromExcel.substring(1, 2) + "/"      //Formatting day, month and year (date contains 01 like)
					+ dateFromExcel.substring(7);
			// When reading from excel in format 01-Mar-2019 but application 3/1/2019
			return date;
		} else {
			String date = months(dateFromExcel.substring(3, 6)) + "/" + dateFromExcel.substring(0, 2) + "/"     //Formatting day, month and year (full dates like 11)
					+ dateFromExcel.substring(7);
			return date;
		}

	}

	public String getCurrentWindowHandle() { //Window handler

		return driver.getWindowHandle(); 

	}

	public String getChildWindowUrl() {   //To get child window url

		String mainWindow = driver.getWindowHandle();
		String url1 = "";
		Set<String> set = driver.getWindowHandles();
		Iterator<String> itr = set.iterator();
		if (itr.hasNext()) {
			while (itr.hasNext()) {
				String childWindow = itr.next();
				if (!mainWindow.equals(childWindow)) {  //Checks if child window is different from main window or not
					driver.switchTo().window(childWindow); // Switching window to child window
					waitForPageAndAjaxToLoad();  // waiting for page and dom load
					url1 = driver.getCurrentUrl(); //Getting current window url
					driver.close();
				}
			}
			driver.switchTo().window(mainWindow); //Switching back to main window.
		}
		return url1;

	}

	public String getWeekeendDates(int month, int year) {  //Get weekend dates

		int y = year;
		Month m = Month.of(month);
		List<Integer> weekendDate = new ArrayList<Integer>();
		IntStream.rangeClosed(1, YearMonth.of(y, m).lengthOfMonth()).mapToObj((day) -> LocalDate.of(y, m, day))  //Creating list of months and years with dates
				.filter(date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)  // Getting weekdays as saturday and sunday
				.forEach(date -> weekendDate.add(date.getDayOfMonth()));  //Adding every saturday and sunday in weekend date.
		String date = month + "/" + weekendDate.get(2) + "/" + year;
		return getDateInFormat(date, "M/d/yyyy", "MM/dd/yyyy");

	}
//        ********************** from  browser Utils *******************

	public static String getElementText_P(WebElement element) {   //Getting visible text from element

		// WebElement element=Driver.getDriver().findElement(locator);
		String text = "";
		if (!element.getText().isEmpty()) { //If element is not empty or null then return text
			text = element.getText();
			logger.info("text on element is " + text);
		}
		return text;

	}

	public static List<String> getElementsTextByPassingElementList(List<WebElement> list) {  //Getting element text from all elements of a list

		List<String> elemTexts = new ArrayList<>();  //Creating list for element text
		for (WebElement el : list) {   //Iterating in the list
			if (!el.getText().isEmpty()) { //Check if list element is empty or not
				elemTexts.add(el.getText()); //Adding texts to the list.
			}
		}
		return elemTexts;

	}

	public static List<String> getElementsTextbyLocator(By locator) {  //Gets texts from locator

		List<WebElement> elems = Driver.getDriver().findElements(locator); //Finding locator by selenium webdriver
		List<String> elemTexts = new ArrayList<>();
		for (WebElement el : elems) {   //Iterating in the list
			if (!el.getText().isEmpty()) {   //Check if list element is empty or not
				elemTexts.add(el.getText());  //Adding texts to the list.
			}
		}
		return elemTexts;

	}

	public void menuIsVisible(WebElement menu, By locator, int retryCount) {  //Checking id menu is visible or not with retry

		int currentRetryCount = 0;  //Initializing retry count variable
		clickAndDraw(menu);  //Highlight before clicking on menu
		try {
			WebElement element = driver.findElement(locator);
			waitFor(1);
			drawborder(element); //Draw border over element
			logger.info("----------------- Menu items are visible -------------------");
			waitFor(1);
			clickAndDraw(menu);
		} catch (NoSuchElementException e) {
			if (currentRetryCount < retryCount) {
				currentRetryCount = currentRetryCount + 1; //Increment count
				logger.info("----------------- Menu items not visible refreshing for time= " + currentRetryCount
						+ " ------------");
				driver.navigate().refresh();
				menuIsVisible(menu, locator, retryCount);
			} else {
				logger.info("Menu items are not visible after 3 retries.");
				// Handle the failure or throw an exception if needed.
			}
		}

	}

	public void waitTillLoadingScreenVanishes() {   //Wait to page fully loaded and disappear screen loading 

		logger.info("Vanishing Loading Screen Method");
		Duration timeout = Duration.ofSeconds(180); // explicit wait 15 sec max limit
//		String loadingImageSelector = ".cs-loader";  // the black screen 
		By loadingImage = By.cssSelector(".cs-loader");
//		waitForNetworkIdle();
		waitFor(1);
		try {
			// Check if the loading image element exists
			if (isElementPresent(loadingImage)) {
				logger.info("Vanishing loading screen method - Loading spinning image is present");
				wait = new WebDriverWait(driver, timeout);
				WebElement loadingImageElement = waitForElement(loadingImage);
				// Wait for either the loading image to vanish or the page to refresh
				wait.until(ExpectedConditions.or(ExpectedConditions.invisibilityOf(loadingImageElement),
						ExpectedConditions.refreshed(ExpectedConditions.stalenessOf(loadingImageElement))));
				logger.info("######## Loading screen is vanished ##############");
			} else {
				logger.info("######## Loading image element not found now. good to go ##############");
			}
		} catch (Exception e) {
			logger.info("######## Loading screen is not vanished, Refreshing the page ##############");
//			driver.navigate().refresh();
			logger.error(LogColor.RED + "Exception occurred: ", e + LogColor.RESET);
			waitTillLoadingScreenVanishes(); // Recursive call to retry after page refresh
		}

	}

	public static void printandlogAllAttributes(WebElement element) {  //Printing all log details of pass, fail, error of html attributes

		JavascriptExecutor js = (JavascriptExecutor) driver; //JavaScript Executor call to command JavaScript
		String script = "var items = {}; " + "for (var i = 0; i < arguments[0].attributes.length; ++i) { "
				+ "    items[arguments[0].attributes[i].name] = arguments[0].attributes[i].value; " + "} "    //Extracting all html attributes from dom elements like name, value. items etc.
				+ "return items;";
		@SuppressWarnings("unchecked")  //Ignore type safety warnings
		Map<String, String> attributes = (Map<String, String>) js.executeScript(script, element); //Maps attribute results
		logger.info("Logging attributes for element: " + element.toString());
		for (Map.Entry<String, String> entry : attributes.entrySet()) {  //Iterating map values both key and value
			logger.info("Attribute: " + entry.getKey() + " = " + entry.getValue());  //Show key and value in log
		}

	}

	public static boolean isPageLoaded(int timeOutInSec) { //Checking id page is loaded or not

		boolean isPageLoaded = false;
		Duration timeOutInSeconds = Duration.ofSeconds(timeOutInSec);
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {  //Implementing Conditions
			public Boolean apply(WebDriver driver) {

				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");  //Checking if document is in ready state or not . If 100% ready then only true. 

			}
		};
		try {
			if (!expectation.equals(true)) {
				logger.info("************************ Page is refreshing *********************************");
				Driver.getDriver().navigate().refresh();  //Refresh page
				WebDriverWait wait = new WebDriverWait(Driver.getDriver(), timeOutInSeconds);
				wait.until(expectation); //Wait till got exception
				if (expectation.equals(true)) {
					logger.info("Page is loaded Successfully");
				}
				isPageLoaded = true; //If loaded then true
			} else if (!expectation.equals(false)) {
				logger.info("Page is Loaded ");
				isPageLoaded = true;  //If no exception then true
			}
			return isPageLoaded;
		} catch (Throwable error) {  //Catching if got any error
			// logger.info(
			// "Timeout waiting for Page Load Request to complete after " + timeOutInSeconds
			// + " seconds");
			return isPageLoaded;
		}

	}

	public static String phoneNumberThreePart(String phoneNumber) { //Formatting 10 digit phone number into 3 seperated part

		String phone = null;
		logger.info("fax number lenght " + phoneNumber.length());
		if (phoneNumber.length() != 0) { //Checking phone number null or not
			phone = phoneNumber.substring(0, 3) + Keys.TAB + phoneNumber.substring(4, 7) + Keys.TAB   //Formatting 3 parts by key indexing
					+ phoneNumber.substring(8);
		} else {
			logger.info("There is no Number.");
		}
		return phone;

	}

	public static void safeSendKeys(WebElement element, String text) {  //Safely enters input value in web element(Interactable)
//	    WebElement element = driver.findElement(locator);

	    // Step 1: Check if element is enabled and displayed properly
	    if (!element.isEnabled() || !element.isDisplayed()) {
	        throw new IllegalStateException("Element located by " + element.toString() + " is disabled and cannot accept input.");
	    }

	    // Step 2: Clear existing text
	    element.clear();

	    // Step 3: Send the text
	    element.sendKeys(text);

	    // Step 4: Verify the text was entered correctly
	    String actualValue = element.getAttribute("value");
	    if (!text.equals(actualValue)) {  //Checking if got correct value with expected and actual value
	        throw new AssertionError("Text mismatch! Expected: " + text + " but found: " + actualValue);
	    }

	    // Optional: Log success
	    logger.info("✅ Text '" + text + "' successfully entered into element: " + element.toString());
	}
	
	/**
	 * Safely select a radio button element.
	 * - Verifies element is enabled and displayed
	 * - Clicks the radio button
	 * - Confirms it is selected
	 * - Logs success or throws an error if selection fails
	 */
	public static void safeSelectRadioButton(WebElement element) {  // Radio button selection safely
	    // Step 1: Check if element is enabled and displayed
	    if (!element.isEnabled() || !element.isDisplayed()) {
	        throw new IllegalStateException("Radio button located by " + element.toString() + " is disabled or not visible.");
	    }

	    // Step 2: Click the radio button
	    element.click();

	    // Step 3: Verify the radio button is selected
	    if (!element.isSelected()) {
	        throw new AssertionError("Radio button selection failed for element: " + element.toString());
	    }

	    // Optional: Log success
	    logger.info("✅ Radio button successfully selected: " + element.toString());
	}

	public static String months(String optionName) {  //Selecting month value from option name

		String month = "";
		switch (optionName) {
		case "Jan":  //For Jan its value will be 1.
			month = "1";
			break;
		case "Feb":
			month = "2";
			break;
		case "Mar":
			month = "3";
			break;
		case "Apr":
			month = "4";
			break;
		case "May":
			month = "5";
			break;
		case "Jun":
			month = "6";
			break;
		case "Jul":
			month = "7";
			break;
		case "Aug":
			month = "8";
			break;
		case "Sep":
			month = "9";
			break;
		case "Oct":
			month = "10";
			break;
		case "Nov":
			month = "11";
			break;
		case "Dec":
			month = "12";
			break;
		}
		return month;

	}
}
