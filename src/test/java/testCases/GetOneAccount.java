package testCases;

import static io.restassured.RestAssured.*;

import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class GetOneAccount extends Authentication {
	String baseURI;
	String getOneAccountEndPoint;
	String headerContentType;
	
	long responseTime;
	String firstAccountId;
	
	public GetOneAccount() {
		baseURI = getProperty("base_uri");
		getOneAccountEndPoint = getProperty("get_one_account_end_point");
		headerContentType = getProperty("header_content_type");
	}
	
	@Test
	public void getOneAccount() {
		Response response = 
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.header("Authorization","Bearer " + generateBearerToken())
			.queryParams("account_id", "677")
			.log().all().
		when()
			.get(getOneAccountEndPoint).
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
		
		JsonPath jp = new JsonPath(responseBody);
		String actualAccountName = jp.getString("account_name");
		String actualAccountNumber = jp.getString("account_number");
		String actualAccountDescription = jp.getString("description");
		String actualAccountBalance = jp.getString("balance");
		String actualAccountContactPerson = jp.getString("contact_person");

		Assert.assertEquals(actualAccountName, "GmyAccount", "Account Name does not match!!");
		Assert.assertEquals(actualAccountNumber, "1234567", "Account Number does not match!!");
		Assert.assertEquals(actualAccountDescription, "This is my account", "Account Description does not match!!");
		Assert.assertEquals(actualAccountBalance, "500.00", "Account Balance does not match!!");
		Assert.assertEquals(actualAccountContactPerson, "Mr.bean", "Account Contact Person does not match!!");
		

	}
	
}
