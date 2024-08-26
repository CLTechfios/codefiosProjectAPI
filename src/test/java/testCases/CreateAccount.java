package testCases;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class CreateAccount extends Authentication {
	String baseURI;
	String createAccountEndPoint;
	String headerContentType;
	String createOneAccountPayloadFilePath;
	String getAllAccountsEndPoint;
	String getOneAccountEndPoint;
	
	long responseTime;
	String firstAccountId;
	
	public CreateAccount() {
		baseURI = getProperty("base_uri");
		createAccountEndPoint = getProperty("create_account_end_point");
		createOneAccountPayloadFilePath = "src/main/java/data/createOneAccountPayload.json";
		headerContentType = getProperty("header_content_type");
		getAllAccountsEndPoint = getProperty("get_all_accounts_end_point");
		getOneAccountEndPoint = getProperty("get_one_account_end_point");
	}
	
	@Test(priority=1)
	public void createAccount() {

		Response response = 
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.header("Authorization","Bearer " + generateBearerToken())
			.body(new File(createOneAccountPayloadFilePath))
			.log().all().
		when()
			.post(createAccountEndPoint).
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
		String accountCreateMessage = jp.getString("message");
		System.out.println(accountCreateMessage);
		
		Assert.assertEquals(accountCreateMessage, "Account created successfully.");
		

	}
	
	@Test(priority=2)
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
		
		String responseBody = response.getBody().asString();
		System.out.println(responseBody);
		
		JsonPath jp = new JsonPath(responseBody);
		firstAccountId = jp.get("records[0].account_id");
		System.out.println("First Account ID: " + firstAccountId);
	}
	@Test(priority=3)
	public void getOneAccount() {
		Response response = 
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.header("Authorization","Bearer " + generateBearerToken())
			.queryParams("account_id", firstAccountId)

			.log().all().
		when()
			.get(getOneAccountEndPoint).
		then()
			.log().all()
			.statusCode(200)
			.header("Content-Type", headerContentType)
			.extract().response();
		
		String responseBody = response.getBody().asString();
		System.out.println(responseBody);
		
		JsonPath jp = new JsonPath(responseBody);
		String actualAccountName = jp.getString("account_name");
		String actualAccountNumber = jp.getString("account_number");
		String actualAccountDescription = jp.getString("description");
		String actualAccountBalance = jp.getString("balance");
		String actualAccountContactPerson = jp.getString("contact_person");

		JsonPath jp2 = new JsonPath(new File(createOneAccountPayloadFilePath));
		String expectedAccountName = jp2.getString("account_name");
		String expectedAccountNumber = jp2.getString("account_number");
		String expectedAccountDescription = jp2.getString("description");
		String expectedAccountBalance = jp2.getString("balance");
		String expectedAccountContactPerson = jp2.getString("contact_person");

		Assert.assertEquals(actualAccountName, expectedAccountName, "Account Name does not match!!");
		Assert.assertEquals(actualAccountNumber, expectedAccountNumber, "Account Number does not match!!");
		Assert.assertEquals(actualAccountDescription, expectedAccountDescription, "Account Description does not match!!");
		Assert.assertEquals(actualAccountBalance, expectedAccountBalance, "Account Balance does not match!!");
		Assert.assertEquals(actualAccountContactPerson, expectedAccountContactPerson, "Account Contact Person does not match!!");
	}
}
