package ru.praktikum.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class StellarApi {
    private final RequestSpecification specification = new RequestSpecBuilder()
            .setBaseUri(System.getProperty("base.url", "https://stellarburgers.education-services.ru"))
            .setBasePath("/api")
            .setContentType(ContentType.JSON)
            .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", 15000)
                    .setParam("http.socket.timeout", 30000)))
            .build();

    public Response register(Map<String, String> user) {
        return step("Создать пользователя", () ->
                given().spec(specification).body(user).post("/auth/register"));
    }

    public Response login(Map<String, String> credentials) {
        return step("Выполнить вход", () ->
                given().spec(specification).body(credentials).post("/auth/login"));
    }

    public Response deleteUser(String accessToken) {
        return step("Удалить тестового пользователя", () ->
                given().spec(specification).header("Authorization", accessToken).delete("/auth/user"));
    }

    public Response ingredients() {
        return step("Получить актуальные ингредиенты", () ->
                given().spec(specification).get("/ingredients"));
    }

    public Response createOrder(List<String> ingredients, String accessToken) {
        return step("Создать заказ", () -> {
            RequestSpecification request = given().spec(specification)
                    .body(Map.of("ingredients", ingredients));
            if (accessToken != null) {
                request.header("Authorization", accessToken);
            }
            return request.post("/orders");
        });
    }
}
