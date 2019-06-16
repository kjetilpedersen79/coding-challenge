package io.bankbridge.handler;

import io.bankbridge.to.Bank;
import org.ehcache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import spark.Request;

import static io.bankbridge.handler.SparkHandler.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BanksCacheBasedTest extends BanksLookupTest<BanksCacheBased> {

    @BeforeEach
    public void beforeEach() {
        banksLookup = new BanksCacheBased();
        request = Mockito.mock(Request.class);
    }

    @Test
    public void thatInitCreatesBanksCache() throws Exception {
        banksLookup.init();
        Cache<String, String> cache = banksLookup.getCache();
        assertNotNull(cache);
    }

    @Test
    public void thatHandleReturnsNameAndIdForBanksInCache() throws Exception {
        banksLookup.init();
        String output = banksLookup.handle(request, null);
        assertAllBanks(output);
    }

    @Test
    public void thatHandleReturnsEmptyOnEmptyCache() throws Exception {
        String output = banksLookup.handle(request, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatHandleReturnsOneBank() throws Exception {
        banksLookup.init("banks-test-v1.json");
        String output = banksLookup.handle(request, null);
        assertBanks(output, new Bank("111", "dummy"));
    }

    @Test
    public void thatNameDummyReturnsEmpty() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("dummy");
        String output = banksLookup.handle(request, null);
        assertEquals("[]", output);
    }

    @AfterEach
    public void afterEach() {
        banksLookup.close();
    }

}
