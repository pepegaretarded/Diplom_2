package ru.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Before;
import org.junit.Test;
import ru.praktikum.support.ApiTestBase;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class LoginUserTest extends ApiTestBase {
    @Before
    public void createUser() {
        user.create();
    }

    @Test
    @DisplayName("Вход существующего пользователя")
    public void registeredUserCanLogin() {
        api.login(user.credentials()).then().statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.email()))
                .body("user.name", equalTo(user.name()))
                .body("accessToken", startsWith("Bearer "))
                .body("refreshToken", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("Вход с неверным email отклоняется")
    public void loginRejectsWrongEmail() {
        Map<String, String> credentials = user.credentials();
        credentials.put("email", UUID.randomUUID() + "@example.com");
        assertInvalidCredentials(credentials);
    }

    @Test
    @DisplayName("Вход с неверным паролем отклоняется")
    public void loginRejectsWrongPassword() {
        Map<String, String> credentials = user.credentials();
        credentials.put("password", "wrong-" + UUID.randomUUID());
        assertInvalidCredentials(credentials);
    }

    @Test
    @DisplayName("Вход с неверными email и паролем отклоняется")
    public void loginRejectsWrongEmailAndPassword() {
        assertInvalidCredentials(Map.of("email", UUID.randomUUID() + "@example.com",
                "password", "wrong-" + UUID.randomUUID()));
    }

    private void assertInvalidCredentials(Map<String, String> credentials) {
        api.login(credentials).then().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
