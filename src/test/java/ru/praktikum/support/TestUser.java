package ru.praktikum.support;

import io.restassured.response.Response;
import ru.praktikum.client.StellarApi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public class TestUser implements AutoCloseable {
    private final StellarApi api;
    private final Map<String, String> data = Map.of(
            "email", "diplom-" + UUID.randomUUID() + "@example.com",
            "password", UUID.randomUUID().toString(),
            "name", "Diploma tester");
    private String accessToken;
    private boolean registrationAttempted;

    public TestUser(StellarApi api) {
        this.api = api;
    }

    public Map<String, String> data() {
        return new HashMap<>(data);
    }

    public Map<String, String> credentials() {
        return new HashMap<>(Map.of("email", data.get("email"), "password", data.get("password")));
    }

    public String email() {
        return data.get("email");
    }

    public String name() {
        return data.get("name");
    }

    public String token() {
        return accessToken;
    }

    public Response register(Map<String, String> payload) {
        registrationAttempted = true;
        Response response = api.register(payload);
        if (response.contentType().contains("json")) {
            String token = response.jsonPath().getString("accessToken");
            if (token != null) {
                accessToken = token;
            }
        }
        return response;
    }

    public void create() {
        register(data()).then().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", not(emptyOrNullString()));
    }

    @Override
    public void close() {
        // Recover the token if a successful registration response was interrupted.
        if (accessToken == null && registrationAttempted) {
            Response login = api.login(credentials());
            if (login.statusCode() == 200) {
                accessToken = login.jsonPath().getString("accessToken");
            }
        }
        if (accessToken != null) {
            api.deleteUser(accessToken).then().statusCode(202).body("success", equalTo(true));
        }
    }
}
