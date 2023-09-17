package com.rafaeldeluca.ecommerce.ecommercerestassured.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
public class ProductControllerRA {
    private Long existingProductId, nonExistingProductId;
    private String productName;

    @BeforeEach
    public void setUp () {
        baseURI = "http://localhost:8080";
        existingProductId = 3L;
        nonExistingProductId = 500L;
        productName = "Rails for Dummies";

    }

    @Test
    void findByIdShouldReturnProductDTOWhenProductIdExists () {
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

    @Test
    void findByIdShouldReturnEntityNotFoundDWhenProductIdDoesNotExist () {
        int nonExistingProductIdInteger = nonExistingProductId.intValue();
        given()
                .get("/products/{id}", nonExistingProductIdInteger)
         .then()
                .statusCode(404);
    }

    @Test
    void findAllShouldReturnPageWithAllProductsWhenProductNameIsEmpty () {
        given()
                .get("products?size=25&page=0&sort=id")
        .then()
                .statusCode(200)
                .body("content.name", hasItems("The Lord of the Rings", "Smart TV","Macbook Pro"))
                .body("last", is(true))
                .body("totalPages", is(1))
                .body("totalElements", is(25))
                .body("pageable.sort.sorted", is(true));
    }

    @Test
    void findAllShouldReturnPageWithSomeProductsWhenProductNameIsNotEmpty () {
        // pode retornar mais de um elemento, por isso  uso o index zero para
        // comparar os dados do primeiro elemento retornado
        given()
                .get("/products?name={productName}", productName)
        .then()
                .statusCode(200)
                .body("content.id[0]", is(5))
                .body("content.price[0]", is(100.99F))
                .body("content.name[0]", equalTo("Rails for Dummies"))
                .body("content.imgUrl[0]", equalTo("https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/5-big.jpg"));
    }
}
