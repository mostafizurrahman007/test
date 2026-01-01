package test.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

public class SecretManager extends CommonMethods {
	

	public static final Logger logger = LogManager.getLogger(CommonMethods.class);
	

	public static String pullSecret () {  //Pulling secrets from aws secret manager

		String secretName = "test";  //Secret name
		Region region = Region.US_EAST_1; // Replace with your AWS region

		SecretsManagerClient secretsClient = SecretsManagerClient.builder().region(region).build();  //Region setup

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build(); //Aws secret setup to request for pulling credentials from

		System.out.println(getSecretValueRequest.toString());  //Printing secret value requesting from aws

		try {   //Error handling if not found
		    logger.debug(secretsClient.getSecretValue(getSecretValueRequest));
			GetSecretValueResponse getSecretValueResponse = secretsClient.getSecretValue(getSecretValueRequest);   //Getting value from aws in string format

			return getSecretValueResponse.secretString();
		} 
		catch (Exception e) {
			e.printStackTrace(); //Error printout
			return null;
		}

	}
	


	public static String getUserName() {  //Getting username 
		// Retrieve secrets
		String secretString = pullSecret();
		// Replace with your secret name)
		JSONObject secretJson = new JSONObject(secretString);  //Converts secret string into object format
		System.out.print(secretJson);
		
		String username = secretJson.getString("userId"); //Getting user id from the secret object
		
		logger.info(LogColor.ThinnerPurple+"userId is: "+ username+LogColor.RESET);
	
		return username;

	}

	public static String getPassword() {  //Getting password
		// Retrieve secrets
		String secretString = pullSecret();
		// Replace with your secret name)
		JSONObject secretJson = new JSONObject(secretString);
		String password = secretJson.getString("password");
		logger.info(LogColor.ThinnerPurple+"password is: "+ password +LogColor.RESET);
		
		return password;

	}
	
	//------------ Database Secret ------------------------//
		public static Map<String, String> getDBSecretDetails() {  //Getting database from aws secret
		    String secretName = ConfigurationReader.getProperty("aws.db.secret.name");
		    Region region = Region.US_EAST_1;

		    SecretsManagerClient secretsClient = SecretsManagerClient.builder()
		        .region(region)
		        .build();

		    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
		        .secretId(secretName)
		        .build();

		    try {
		        GetSecretValueResponse getSecretValueResponse = secretsClient.getSecretValue(getSecretValueRequest);
		        String secretString = getSecretValueResponse.secretString();
		        JSONObject secretJson = new JSONObject(secretString);

		        Map<String, String> secretMap = new HashMap<>();  //Storing key value pair
		        secretMap.put("host", secretJson.getString("host"));
		        secretMap.put("port", secretJson.getString("port"));
		        secretMap.put("username", secretJson.getString("username"));
		        secretMap.put("password", secretJson.getString("password"));

		        return secretMap;
		    } catch (SecretsManagerException e) {
		        logger.error("Error retrieving DB secret", e);
		        return Collections.emptyMap();
		    }
		}

}
