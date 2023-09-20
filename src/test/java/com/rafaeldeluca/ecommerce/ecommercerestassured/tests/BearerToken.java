package com.rafaeldeluca.ecommerce.ecommercerestassured.tests;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BearerToken {

    private static final String tokenUrl = "/oauth2/token";

    private static Response authorizationRequest (String username, String password) {
        return
                given()
                .auth()
                        .preemptive()
                        .basic("myclientid", "myclientsecret")
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("grant_type","password")
                        .formParam("username", username)
                        .formParam("password", password)
                .when()
                        .post(tokenUrl);
    }

    private static String obtainBearerToken(String username, String password) {
        Response response = authorizationRequest(username, password);
        JsonPath jsonPath = response.jsonPath();
        return jsonPath.getString("access_token");
    }
}
