package ru.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.praktikum.support.ApiTestBase;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class CreateOrderTest extends ApiTestBase {
    private final boolean authorized;

    public CreateOrderTest(boolean authorized) {
        this.authorized = authorized;
    }

    @Parameterized.Parameters(name = "authorized={0}")
    public static Collection<Object[]> authorizationCases() {
        return Arrays.asList(new Object[][] {{true}, {false}});
    }

    private String token() {
        if (authorized) {
            user.create();
        }
        return user.token();
    }

    @Test
    @DisplayName("Заказ с актуальными ингредиентами")
    public void orderWithIngredientsCanBeCreated() {
        List<String> ingredients = api.ingredients().then().statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("data.size()", greaterThan(1))
                .extract().jsonPath().getList("data._id", String.class);
        api.createOrder(ingredients.subList(0, 2), token()).then().statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Заказ без ингредиентов отклоняется")
    public void orderRequiresIngredients() {
        api.createOrder(Collections.emptyList(), token()).then().statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с неверным хешем ингредиента возвращает ошибку")
    public void orderRejectsInvalidIngredientHash() {
        api.createOrder(List.of("invalid-ingredient-hash"), token()).then().statusCode(SC_INTERNAL_SERVER_ERROR)
                .body(containsString("Internal Server Error"));
    }
}
