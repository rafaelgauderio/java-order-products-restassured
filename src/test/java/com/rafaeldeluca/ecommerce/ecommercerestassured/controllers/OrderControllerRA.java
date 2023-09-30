package com.rafaeldeluca.ecommerce.ecommercerestassured.controllers;

import com.rafaeldeluca.ecommerce.ecommercerestassured.tests.BearerToken;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerRA {

    private Long existingOrderId, nonExistingOrderID, adminOrderId;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;

    @BeforeEach
    public void setUp() {

        baseURI = "http://localhost:8080";

        existingOrderId = 1L;
        adminOrderId = 2L;
        nonExistingOrderID = 50L;

        clientUsername = "carolina@gmail.com";
        adminUsername = "rafaeldeluca@gmail.com";
        clientPassword = adminPassword = "123456";

        clientToken = BearerToken.obtainBearerToken(clientUsername, clientPassword);
        adminToken = BearerToken.obtainBearerToken(adminUsername, adminPassword);
        invalidToken = clientToken + "invalidString";
    }

    @Test
    void findByIdShouldReturnOrderWhenOrderIdExistsAndUserLoggedAsAdmin() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", equalTo("PAID"))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("client.name", equalTo("Carolina de Luca"))
                .body("payment.id", is(1))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
                .body("items.productId", hasItems(1, 3))
                .body("total", is(1431.0F));
    }

    @Test
    void findByIdShouldReturnOrderWhenOrderIdExistsAndUserLoggedAsClientAndOrderBelowsToClient() {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}", existingOrderId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("status", equalTo("PAID"))
                .body("moment", equalTo("2022-07-25T13:00:00Z"))
                .body("client.name", equalTo("Carolina de Luca"))
                .body("payment.id", is(1))
                .body("payment.moment", equalTo("2022-07-25T15:00:00Z"))
                .body("items.productId", hasItems(1, 3))
                .body("items.name", hasItems("The Lord of the Rings", "Macbook Pro"))
                .body("total", is(1431.0F));
    }

    @Test
    void findByIdShouldReturnOrderWhenIdExistsAndUserLoggedAsClientAndOrderBelongsToClientOtherClient() {

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders/{id}", adminOrderId)
        .then()
                .statusCode(403);
    }
    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndUserLoggedAsClient () {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .accept(ContentType.JSON)
        .when()
                .get("/orders{id}" ,nonExistingOrderID)
         .then()
                .statusCode(404); // Not Found
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndUserLoggedAsAdmin () {
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .accept(ContentType.JSON)
                .when()
                .get("/orders/{id}" ,nonExistingOrderID)
                .then()
                .statusCode(404); // Not Found
    }


}
