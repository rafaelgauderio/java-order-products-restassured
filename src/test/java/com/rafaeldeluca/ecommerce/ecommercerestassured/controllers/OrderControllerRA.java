package com.rafaeldeluca.ecommerce.ecommercerestassured.controllers;

import com.rafaeldeluca.ecommerce.ecommercerestassured.tests.BearerToken;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.*;
// import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
public class OrderControllerRA {

    private Long existingOrderId, nonExistingOrderID;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;

    @BeforeEach
    void setUp() {

        baseURI = "http://localhost:8080";

        existingOrderId = 1L;
        nonExistingOrderID = 50L;

        clientUsername = "carolina@gmail.com";
        adminUsername = "rafaeldeluca@gmail.com";
        clientPassword = adminPassword= "123456";

        clientToken = BearerToken.obtainBearerToken(clientUsername, clientPassword);
        adminToken = BearerToken.obtainBearerToken(adminUsername, adminPassword);
        invalidToken = clientToken + "invalidString";
    }


}
