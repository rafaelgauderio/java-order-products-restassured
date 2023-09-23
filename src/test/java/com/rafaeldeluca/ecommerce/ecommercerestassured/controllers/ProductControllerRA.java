package com.rafaeldeluca.ecommerce.ecommercerestassured.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rafaeldeluca.ecommerce.ecommercerestassured.tests.BearerToken;
import io.restassured.http.ContentType;
import org.hamcrest.StringDescription;
import org.json.JSONObject;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;
public class ProductControllerRA {
    private Long existingProductId, nonExistingProductId;
    private String productName;
    private String clientUsername, clientPassword, adminUsername, adminPassword;
    private String clientToken, adminToken, invalidToken;
    public Map<String,Object> postProductMap, category1, category2;

    public List<Map<String, Object>> categoriesList;


    @BeforeEach
    public void setUp () {
        baseURI = "http://localhost:8080";
        existingProductId = 3L;
        nonExistingProductId = 500L;
        productName = "Rails for Dummies";

        clientUsername = "carolina@gmail.com";
        adminUsername = "rafaeldeluca@gmail.com";
        clientPassword = "123456";
        adminPassword = "123456";

        clientToken = BearerToken.obtainBearerToken(clientUsername, clientPassword);
        adminToken = BearerToken.obtainBearerToken(adminUsername, adminPassword);
        invalidToken = String.valueOf(new StringBuilder(clientToken).append("invalidString"));

        postProductMap = new HashMap<String, Object>();
        postProductMap.put("name", "Tablet Sansung");
        postProductMap.put("description", "Tablet Sansung, core i5, 4 Giga ram");
        postProductMap.put("imgUrl", "https://melhoramentoshigieners.com.br/tablet.png");
        postProductMap.put("price", 2500.90);


        categoriesList = new ArrayList<Map<String, Object>>();
        category1 = new HashMap<String, Object>();
        category1.put("id",2);
        category2 = new HashMap<String, Object>();
        category2.put("id",3);
        categoriesList.add(category1);
        categoriesList.add(category2);

        postProductMap.put("categories", categoriesList);



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
                .get("/products?size=30&page=0&sort=id")
        .then()
                .statusCode(200)
                .body("content.name", hasItems("The Lord of the Rings", "Smart TV","Macbook Pro"))
                .body("last", is(true))
                .body("totalPages", is(1))
                //.body("totalElements", is(26))
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

    @Test
    void findAllShouldReturnPageWhenProductPriceIsGreaterThen2200 () {
        given()
                .get("/products?size=25")
        .then()
                .statusCode(200)
                .body("content.findAll {it.price > 2200}.name", hasItems("PC Gamer Hera","PC Gamer Max", "PC Gamer Min"));
    }

    @Test
    void findAllShouldReturnPageWithOneProductWhenProductPriceIsEqualTo90_5 () {
        given()
                .get("/products?size=25")
                .then()
                .statusCode(200)
                .body("content.findAll {it.price <= 90.5}.name", hasItems("The Lord of the Rings"));
    }

    @Test()
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    void insertShouldCreateNewProductWhenUserLoggedAsAdmin () throws JsonParseException {
        JSONObject newProduct = new JSONObject(postProductMap);

        //System.out.println(postProductMap);
        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
         .then()
                .statusCode(201)
                .body("name", equalTo("Tablet Sansung"))
                .body("description", equalTo("Tablet Sansung, core i5, 4 Giga ram"))
                .body("imgUrl", equalTo("https://melhoramentoshigieners.com.br/tablet.png"))
                .body("price", is(2500.90F))
                .body("categories.id", hasItems(2,3));

    }
    @Test()
    void insertShouldReturnUnprocessableEntityWhenWhenUserLoggedAsAdminAndInvalidName ()  {
        String invalidName = "Ra";
        postProductMap.put("name", invalidName);
        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Nome precisa ter de 3 a 80 caracteres"));
    }

    @Test()
    void insertShouldReturnUnprocessableEntityWhenWhenUserLoggedAsAdminAndInvalidDescription ()  {
        String invalidDescription = "invalid";
        postProductMap.put("description", invalidDescription);
        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Descrição precisa ter no mínimo 10 caracteres"));
    }

    @Test()
    void insertShouldReturnUnprocessableEntityWhenWhenUserLoggedAsAdminAndPriceIsZero ()  {
        Double invalidPrice = 0.0;
        postProductMap.put("price", invalidPrice);
        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }
    @Test
    void insertShouldReturnUnprocessableEntityWhenUserLoggerAsAdminAndPriceIsNegative () {
        Double negativePrice = -500.99;
        postProductMap.put("price", negativePrice);
        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
        .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("O preço deve ser positivo"));
    }

    @Test
    void insertShouldReturnUnprocessableEntityWhenUserLoggerAsAdminAndNoCategoryIsAssociateToProduct () {
        categoriesList = null;
        postProductMap.put("categories", categoriesList);
        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(422)
                .body("errors.message[0]", equalTo("Produto deve ter pelo menos uma categoria"))
                .body("errors.fieldName", hasItems("categories"));

    }

    @Test
    void insertShouldReturnForbiddenWhenUserLoggerAsClient () {
        // can not insert a product when logged with a client token
        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + clientToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(403);
    }

    @Test
    void insertShouldReturnForbiddenWhenUserTryToLoggedWithInvalidToken () {

        JSONObject newProduct = new JSONObject(postProductMap);

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newProduct.toString())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/products")
                .then()
                .statusCode(401);
    }
}
