package testCases;

import util.ConfigReader;
import io.restassured.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Authentication extends ConfigReader {
	String baseURI;
	String authenticationEndPoint;
	String authorizationFilePath;//
	String headerContentType;
	
	long responseTime;
	String bearerToken;
	
	public Authentication() {
		baseURI = getProperty("base_uri");
		authenticationEndPoint = getProperty("authentication_end_point");
		authorizationFilePath = "src\\main\\java\\data\\authorizationPayLoad.json";
		headerContentType = getProperty("header_content_type");

	}

	public boolean validateResponseTime() {
		boolean withinRange = false;
		if (responseTime <= 2000) {
			System.out.println("The response time is within range.");
			withinRange = true;
		} else {
			System.out.println("The response time is out of range.");
		}
		return withinRange;
	}
	
	public String generateBearerToken() {//

		Response response =
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.body(new File(authorizationFilePath))
			.log().all().
		when()
			.post(authenticationEndPoint).
		then()
			.log().all()
			.statusCode(201)
			.header("Content-Type", headerContentType)
			.extract().response();
		 
		int statusCode = response.getStatusCode();
		System.out.println("Status code: " + statusCode);
		Assert.assertEquals(statusCode, 201, "Status code dues not match!");
		
		long responseTime = response.timeIn(TimeUnit.MILLISECONDS);
		System.out.println("Response Time: " + responseTime);
		Assert.assertEquals(validateResponseTime(), true);
		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Header Content Type: " + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Response Header Content Type does not match!!");
		
		String responseBody = response.getBody().asString();
		System.out.println(responseBody);
		JsonPath jp = new JsonPath(responseBody);
		bearerToken = jp.get("access_token");
		System.out.println("Bearer Token: " + bearerToken);
		return bearerToken;
	}
}
