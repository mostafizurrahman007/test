package test.utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class Driver {
	 public Driver() { 
		 // Constructor used for various purposes like object initialization, inheritance, overloading etc.
	 }
	 
	 public static WebDriver driver;
	 public static final Logger logger = LogManager.getLogger(Driver.class);
	 
	 // Default download path
	 public static String downloadFolderPath = System.getProperty("user.dir") + "\\Downloads";
	 public static String browser = System.getProperty("browser", "chrome");
	 
	 public static WebDriver getDriver() {
		 
		 if (driver == null){
			 
			 if(browser == null || browser.isBlank()) {
				 logger.info(LogColor.RED+"Current Browser is null so lunching Edge by default for Pipeline"+LogColor.RESET);
				 
				 browser = "eedge";
			 }
			 
			 EdgeOptions edgeOptions = new EdgeOptions();
			 ChromeOptions chromeOptions = new ChromeOptions();
			 
			 HashMap<String, Object> edgePrefs = new HashMap<>();
			 HashMap<String, Object> chromePrefs = new HashMap<>();
			 
			 switch (browser) {
			case "edge":
				
				// Set up preferences
				edgePrefs.put("download.default_directory", downloadFolderPath);
				edgePrefs.put("profile.default_content_settings.popups", 0);
				edgePrefs.put("profile.default_zoom_level", 0);
				edgePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
				edgePrefs.put("download.prompt_for_download", false);
				
				edgeOptions.setExperimentalOption("prefs", edgePrefs);

                // Arguments: 				
				edgeOptions.addArguments("--inprivate"); // Enable headless mode. Incognito mode.
				edgeOptions.addArguments("disable-gpu"); // Disable GPU acceleration
				edgeOptions.addArguments("--disable-notifications");
				edgeOptions.addArguments("--no-download-notification");
				edgeOptions.addArguments("--window-size=1920,1080");
				edgeOptions.addArguments("--force-device-scale-factor=1");
				edgeOptions.addArguments("--high-dpi-support=1");

				// Generate a unique user data directory
				logger.info("creating temp directory in -Edge");
				String tempUserDataDir = System.getProperty("java.io.tmpdir") + "/edge-profile-" + UUID.randomUUID();
				edgeOptions.addArguments("--user-data-dir=" + tempUserDataDir);
				// Save it for cleanup
				System.setProperty("edge.temp.profile", tempUserDataDir);

				// launch Browser
				driver = new EdgeDriver(edgeOptions);

				break;
				
			case "chrome":
				
				// Set up preferences
				chromePrefs.put("download.default_directory", downloadFolderPath); //Sets default download folder
				chromePrefs.put("profile.default_content_settings.popups", 0); //Blocks popup windows
				chromePrefs.put("profile.default_zoom_level", 0); //Sets browser zoom to 100%
				chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1); //Allows multiple automatic downloads. 1 means allowed. 0 means blocked.
				chromePrefs.put("download.prompt_for_download", false); // Disables "Save As" dialog. True will enable.

				chromeOptions.setExperimentalOption("prefs", chromePrefs);

				// Arguments
				chromeOptions.addArguments("--incognito"); // Chrome equivalent of Edge's inprivate. Chrome's incognito mode.
				chromeOptions.addArguments("--disable-gpu"); // Disable GPU acceleration
				chromeOptions.addArguments("--disable-notifications"); //Blocks browser notifications
				chromeOptions.addArguments("--no-download-notification"); //Hides download notifications
				chromeOptions.addArguments("--window-size=1920,1080"); //Sets browser window dimensions. Consistent viewport for all tests.
				chromeOptions.addArguments("--force-device-scale-factor=1"); // Disables display scaling. No zoom. Default 100% screen.
				chromeOptions.addArguments("--high-dpi-support=1"); //Enable high-DPI (dots per inch) support for crisp text and images. 4k like Mac displays view.

				// Generate a unique user data directory
				logger.info("creating temp directory in -Chrome");
				String tempUserDataDirchrome = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + UUID.randomUUID();
				chromeOptions.addArguments("--user-data-dir=" + tempUserDataDirchrome);

				// Save it for cleanup
				System.setProperty("chrome.temp.profile", tempUserDataDirchrome);

				// Launch Browser
				driver = new ChromeDriver(chromeOptions);
				break;
				
			case "chrome-headless":

			    // Set up preferences:
			    chromePrefs.put("download.default_directory", downloadFolderPath);
			    chromePrefs.put("profile.default_content_settings.popups", 0);
			    chromePrefs.put("profile.default_zoom_level", 0);
			    chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
			    chromePrefs.put("download.prompt_for_download", false);

			    chromeOptions.setExperimentalOption("prefs", chromePrefs);

			    // Arguments:
			    chromeOptions.addArguments("--headless=new"); // Enable headless mode (new headless for latest Chrome)
			    chromeOptions.addArguments("--incognito");    // Chrome equivalent of Edge's inprivate
			    chromeOptions.addArguments("--disable-gpu");  // Disable GPU acceleration
			    chromeOptions.addArguments("--no-sandbox"); //Run Chrome without security barriers
			    chromeOptions.addArguments("--disable-dev-shm-usage"); //Don't use shared memory, use disk instead (fixes crashes in Docker/Linux).
			    chromeOptions.addArguments("--window-size=1920,1080");
			    chromeOptions.addArguments("--force-device-scale-factor=0.75"); //Make Websites appear at 75% zoom
			    chromeOptions.addArguments("--high-dpi-support=1"); //

			    // Generate a unique user data directory
			    logger.info("creating temp directory in -chrome_headless");
			    String tempUserDataDirHeadless = System.getProperty("java.io.tmpdir") + "/chrome-profile-" + UUID.randomUUID();
			    chromeOptions.addArguments("--user-data-dir=" + tempUserDataDirHeadless);

			    // Save it for cleanup
			    System.setProperty("chrome.temp.profile", tempUserDataDirHeadless);

			    // Launch Browser
			    driver = new ChromeDriver(chromeOptions);
			    break;
			    
			case "edge-headless":

                // Set up preferences:
				edgePrefs.put("download.default_directory", downloadFolderPath);
				edgePrefs.put("profile.default_content_settings.popups", 0);
				edgePrefs.put("profile.default_zoom_level", 0);
				edgePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
				edgePrefs.put("download.prompt_for_download", false);
				edgeOptions.setExperimentalOption("prefs", edgePrefs);

                 //	Arguments:			
				edgeOptions.addArguments("--headless=new"); // Enable headless mode
				edgeOptions.addArguments("--inprivate");
				edgeOptions.addArguments("disable-gpu"); // Disable GPU acceleration
				edgeOptions.addArguments("--no-sandbox");
				edgeOptions.addArguments("--disable-dev-shm-usage");
				edgeOptions.addArguments("--window-size=1920,1080");
				edgeOptions.addArguments("--force-device-scale-factor=0.75");
				edgeOptions.addArguments("--high-dpi-support=1");

                // Generate a unique user data directory
				logger.info("creating temp directory in -edge_headless");
				String tempUserDataDirheadless = System.getProperty("java.io.tmpdir") + "/edge-profile-"
						+ UUID.randomUUID();
				edgeOptions.addArguments("--user-data-dir=" + tempUserDataDirheadless);

                // Save it for cleanup
				System.setProperty("edge.temp.profile", tempUserDataDirheadless);

				driver = new EdgeDriver(edgeOptions);

                // devicce matrics

				break;
			}
		 }
		 
		 driver.manage().deleteAllCookies();
		 return driver;
	 }
	 
	public static void closeDriver() {
		if (driver != null) {
			driver.quit();
			driver = null;
		}
	}
	
	public static void BrowserSetup() {
		logger.info(LogColor.RED+"Browser Setup"+LogColor.RESET);
		
		Driver.getDriver().manage().deleteAllCookies();
		Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
		Driver.getDriver().manage().window().maximize();
		
		String URL = ConfigurationReader.getProperty("url");
		Driver.getDriver().get(URL);
		logger.info("URL Lunched: " + ConfigurationReader.getProperty("url"));
		CommonMethods.waitForPageAndAjaxToLoad();
	}
}