package ru.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.praktikum.support.ApiTestBase;

import java.util.Map;

import static org.hamcrest.Matchers.*;

public class CreateUserTest extends ApiTestBase {
    @Test
    @DisplayName("Регистрация уникального пользователя")
    public void uniqueUserCanRegister() {
        user.register(user.data()).then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.email()))
                .body("user.name", equalTo(user.name()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Повторная регистрация отклоняется")
    public void duplicateUserCannotRegister() {
        user.create();
        user.register(user.data()).then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без email отклоняется")
    public void registrationRequiresEmail() {
        assertMissingField("email");
    }

    @Test
    @DisplayName("Регистрация без пароля отклоняется")
    public void registrationRequiresPassword() {
        assertMissingField("password");
    }

    @Test
    @DisplayName("Регистрация без имени отклоняется")
    public void registrationRequiresName() {
        assertMissingField("name");
    }

    private void assertMissingField(String field) {
        Map<String, String> payload = user.data();
        payload.remove(field);
        user.register(payload).then().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
