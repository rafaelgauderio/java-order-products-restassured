package com.rafaeldeluca.ecommerce.ecommercerestassured.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
public class CategoryControllerRA {

    @BeforeEach
    public void setup () {
        baseURI = "http://localhost:8080";
    }

    @Test
    void fillAllShouldRetunrListWithAllCategories () {
        given().
                get("/categories")
                .then()
                .statusCode(200)
                .body("id", hasItems(1,2,3,4))
                .body("name", hasItems("Livros" ,"Eletrônicos" ,"Computadores","Televisores" ));
    }
}
