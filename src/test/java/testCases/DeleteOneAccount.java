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

public class DeleteOneAccount extends Authentication {
//	Key list
	String baseURI;
	String updateOneAccountEndPoint;
	String headerContentType;
	String updateOneAccountPayloadFilePath;
	String getAllAccountsEndPoint;
	String getOneAccountEndPoint;
	String deleteOneAccountEndPoint;
	String deleteOneAccountId;
	Map<String,String> updateAccountPayloadMap;
	
	long responseTime;
	String firstAccountId;
	
	public DeleteOneAccount() {
		baseURI = getProperty("base_uri");
		deleteOneAccountEndPoint = getProperty("delete_one_account_end_point");
		updateAccountPayloadMap = new HashMap<String,String>();// 
		headerContentType = getProperty("header_content_type");
		getOneAccountEndPoint = getProperty("get_one_account_end_point");
		deleteOneAccountId = "679";
	}
		
	@Test(priority=1)
	public void deleteOneAccount() {

		Response response =
		given()
			.baseUri(baseURI)
			.header("Content-Type", headerContentType)
			.header("Authorization","Bearer " + generateBearerToken())
			.queryParam("account_id", deleteOneAccountId)
			.log().all().
		when()
			.delete(deleteOneAccountEndPoint).
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
		String accountDeleteMessage = jp.getString("message");
		System.out.println(accountDeleteMessage);
		Assert.assertEquals(accountDeleteMessage, "Account deleted successfully.");

		}
		@Test(priority=2)
		public void getOneAccount() {
			Response response = 
			given()
				.baseUri(baseURI)
				.header("Content-Type", headerContentType)
				.header("Authorization","Bearer " + generateBearerToken())
				.queryParams("account_id", deleteOneAccountId)
				.log().all().
			when()
				.get(getOneAccountEndPoint).
			then()
				.log().all()
				.statusCode(404)
				.header("Content-Type", headerContentType)
				.extract().response();
			 
			int statusCode = response.getStatusCode();
			System.out.println("Status code: " + statusCode);
			Assert.assertEquals(statusCode, 404, "Status code dues not match!");
			
			String responseBody = response.getBody().asString();
			System.out.println(responseBody);
			
			JsonPath jp = new JsonPath(responseBody);
			String noRecordFoundMessage = jp.getString("message");
			System.out.println(noRecordFoundMessage);
			Assert.assertEquals(noRecordFoundMessage, "No Record Found");

	}
	
}
