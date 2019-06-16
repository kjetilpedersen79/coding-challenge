package io.bankbridge.handler;

import io.bankbridge.MockRemotes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import spark.Request;

public class BanksRemoveCallsTest extends BanksLookupTest<BanksRemoteCalls> {

    @BeforeAll
    public static void beforeAll() {
        MockRemotes.main(null);
    }

    @BeforeEach
    public void beforeEach() {
        banksLookup = new BanksRemoteCalls();
        request = Mockito.mock(Request.class);
    }

    @AfterEach
    public void afterEach() {
        banksLookup.close();
    }

}
