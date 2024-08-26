package testCases;

import static io.restassured.RestAssured.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UpdateOneAccount extends Authentication {
	String baseURI;
	String updateOneAccountEndPoint;
	String headerContentType;
	String updateOneAccountPayloadFilePath;
	String getAllAccountsEndPoint;
	String getOneAccountEndPoint;
	Map<String,String> updateAccountPayloadMap;
	

	long responseTime;
	String firstAccountId;
	
	public UpdateOneAccount() {
		baseURI = getProperty("base_uri");
		updateOneAccountEndPoint = getProperty("update_one_account_end_point");
		updateAccountPayloadMap = new HashMap<String,String>();// 
		headerContentType = getProperty("header_content_type");
		getOneAccountEndPoint = getProperty("get_one_account_end_point");
	}
	
	public Map<String,String> updateAccountBodyMap(){
		updateAccountPayloadMap.put("account_id", "680");
		updateAccountPayloadMap.put("account_name", "BB Que");
		updateAccountPayloadMap.put("account_number", "747");
		updateAccountPayloadMap.put("description", "API test update account Rest Assured");
		updateAccountPayloadMap.put("balance", "961.69");
		updateAccountPayloadMap.put("contact_person", "BestGrill");
		return updateAccountPayloadMap;
	}
	
	@Test(priority=1)
	public void updateOneAccount() {
		Response response =
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.header("Authorization","Bearer " + generateBearerToken())
			.body(updateAccountBodyMap())
			.log().all().
		when()
			.put(updateOneAccountEndPoint).
		then()
			.log().all()
			.statusCode(200)
			.header("Content-Type", headerContentType)
			.extract().response();
		 
		int statusCode = response.getStatusCode();
		System.out.println("Status code: " + statusCode);
		Assert.assertEquals(statusCode, 200, "Status code does not match!");
		
		long responseTime = response.timeIn(TimeUnit.MILLISECONDS);
		System.out.println("Response Time: " + responseTime);
		Assert.assertEquals(validateResponseTime(), true);

		String responseHeaderContentType = response.getHeader("Content-Type");
		System.out.println("Header Content Type: " + responseHeaderContentType);
		Assert.assertEquals(responseHeaderContentType, headerContentType, "Response Header Content Type does not match!!");
		
		String responseBody = response.getBody().asString();
		System.out.println(responseBody);
		
		JsonPath jp = new JsonPath(responseBody);
		String accountUpdateMessage = jp.getString("message");
		System.out.println(accountUpdateMessage);
		Assert.assertEquals(accountUpdateMessage, "Account updated successfully.");

		}
		@Test(priority=2)
		public void getOneAccount() {
			Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", headerContentType)
				.header("Authorization","Bearer " + generateBearerToken())
				.queryParams("account_id", updateAccountBodyMap().get("account_id"))
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
			System.out.println(responseBody);
			
			JsonPath jp = new JsonPath(responseBody);
			String actualAccountID = jp.getString("account_id");
			String actualAccountName = jp.getString("account_name");
			String actualAccountNumber = jp.getString("account_number");
			String actualAccountDescription = jp.getString("description");
			String actualAccountBalance = jp.getString("balance");
			String actualAccountContactPerson = jp.getString("contact_person");

			String expectedAccountID = updateAccountBodyMap().get("account_id");
			String expectedAccountName = updateAccountBodyMap().get("account_name");
			String expectedAccountNumber = updateAccountBodyMap().get("account_number");
			String expectedAccountDescription = updateAccountBodyMap().get("description");
			String expectedAccountBalance = updateAccountBodyMap().get("balance");
			String expectedAccountContactPerson = updateAccountBodyMap().get("contact_person");
			
			Assert.assertEquals(actualAccountID, expectedAccountID, "Account ID does not match!!");
			Assert.assertEquals(actualAccountName, expectedAccountName, "Account Name does not match!!");
			Assert.assertEquals(actualAccountNumber, expectedAccountNumber, "Account Number does not match!!");
			Assert.assertEquals(actualAccountDescription, expectedAccountDescription, "Account Description does not match!!");
			Assert.assertEquals(actualAccountBalance, expectedAccountBalance, "Account Balance does not match!!");
			Assert.assertEquals(actualAccountContactPerson, expectedAccountContactPerson, "Account Contact Person does not match!!");
		
	}
	
}
