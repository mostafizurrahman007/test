package test.utils;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationReader {
	 
	private static Properties configFile;  // Storage box for configuration settings named as configFile. Holds data in pair.
	public static final Logger logger = LogManager.getLogger(ConfigurationReader.class); // Log configuration for getting log message.

	static {   //Runs before any method calls

		try {
            String path = "Configuration.properties"; 
			 String profile = System.getProperty("profile"); // Get active profile 
				logger.info(LogColor.Magenta+"****************** Current Profile: "+profile+"******************"+LogColor.RESET);
	
			 
			 
	            if (profile == null) {
	                // Default to configuration.properties if no profile is specified
	                path = "Configuration.properties";
	                logger.info("Current configFile ="+path);
	            }
	            else {
	                // Load profile-specific configuration
	                path = profile + ".properties";
	                logger.info("Current configFile ="+path);
	            }
			 
			FileInputStream input = new FileInputStream(path); //Open the configuration file for reading from the specific path.

			configFile = new Properties(); //Creates property object empty container for key-values
			configFile.load(input); //Parses file and load/populates Properties object

			input.close(); //Releases file handle (good practice)
		} catch (Exception e) { //Prevents application crash if config file missing
			e.printStackTrace(); //Prints stack trace to console
		}
		}

	public static String getProperty(String keyName) { //helper method to get any setting value by calling its name. Return String only.
		return configFile.getProperty(keyName);  //Look up the key in the settings dictionary and return its value.
	}
}
