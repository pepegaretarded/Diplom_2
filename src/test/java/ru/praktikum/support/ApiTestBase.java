package ru.praktikum.support;

import org.junit.After;
import org.junit.Before;
import ru.praktikum.client.StellarApi;

public abstract class ApiTestBase {
    protected StellarApi api;
    protected TestUser user;

    @Before
    public void setUp() {
        api = new StellarApi();
        user = new TestUser(api);
    }

    @After
    public void cleanUp() {
        if (user != null) {
            user.close();
        }
    }
}
