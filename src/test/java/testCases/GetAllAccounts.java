package testCases;

import static io.restassured.RestAssured.*;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetAllAccounts extends Authentication {
	String baseURI;
	String getAllAccountsEndPoint;
	String headerContentType;
	
	long responseTime;
	String firstAccountId;
	
	public GetAllAccounts() {
		baseURI = getProperty("base_uri");
		getAllAccountsEndPoint = getProperty("get_all_accounts_end_point");
		headerContentType = getProperty("header_content_type");
	}
	
	@Test
	public void getAllAccounts() {
		Response response = 
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.auth().preemptive().basic("demo@codefios.com", "abc123")
			.log().all().
		when()
			.get(getAllAccountsEndPoint).
		then()
			.log().all()
			.statusCode(200)
			.header("Content-Type", headerContentType)
			.extract().response();
		 
		int statusCode = response.getStatusCode();
		System.out.println("Status code: " + statusCode);
		Assert.assertEquals(statusCode, 200, "Status code dues not match!");
		
		long responseTime = response.timeIn(TimeUnit.MILLISECONDS);
		System.out.println("Response Time: " + responseTime);
		Assert.assertEquals(validateResponseTime(), true);

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Header Content Type: " + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Response Header Content Type does not match!!");
		
		String responseBody = response.getBody().asString();
		System.out.println(responseBody);
		
		JsonPath jp = new JsonPath(responseBody);
		firstAccountId = jp.get("records[0].account_id");
		System.out.println("First Account ID: " + firstAccountId);
		
		if (firstAccountId != null) {
			System.out.println("First Account ID is NOT NULL");
		} else {
			System.out.println("First Account ID is NULL");
		}
	}
	
}
