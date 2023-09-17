package com.rafaeldeluca.ecommerce.ecommercerestassured.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
public class ProductControllerRA {
    private Long existingProductId, nonExistingProductId;

    @BeforeEach
    public void setUp () {
        baseURI = "http://localhost:8080";
        existingProductId = 3L;

    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists () {
        int existingProductIdInteger = existingProductId.intValue();
        given()
                .get("/products/{id}", existingProductIdInteger)
        .then()
                .statusCode(200)
                .body("id", is(existingProductIdInteger))
                .body("price", is(1250.0F))
                .body("name", equalTo("Macbook Pro"))
                .body("imgUrl", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg"))
                .body("categories.id", hasItems(2,3))
                .body("categories.name", hasItems("Computadores", "Eletrônicos"));
    }

}
